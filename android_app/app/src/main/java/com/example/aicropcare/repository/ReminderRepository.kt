package com.example.aicropcare.repository

import android.content.Context
import android.util.Log
import com.example.aicropcare.data.database.ReminderDatabaseHelper
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.data.models.ReminderType
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.notifications.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderRepository(private val context: Context) {

    private val dbHelper = ReminderDatabaseHelper(context)
    private val sessionManager = SessionManager(context)

    companion object {
        private const val TAG = "ReminderRepository"
    }

    private fun getCurrentUserId(): Int {
        val uid = sessionManager.userId
        return if (uid > 0) uid else 1
    }

    suspend fun createReminder(
        title: String,
        cropName: String?,
        reminderType: ReminderType,
        reminderTimestamp: Long,
        formattedDate: String,
        formattedTime: String,
        notes: String?,
        weatherPrecaution: String?
    ): Long = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserId()
            val reminder = FarmingReminder(
                userId = userId,
                title = title.trim(),
                cropName = cropName?.trim()?.ifBlank { null },
                reminderType = reminderType,
                reminderTimestamp = reminderTimestamp,
                formattedDate = formattedDate,
                formattedTime = formattedTime,
                notes = notes?.trim()?.ifBlank { null },
                weatherPrecaution = weatherPrecaution?.trim()?.ifBlank { null },
                isCompleted = false
            )
            val insertedId = dbHelper.insertReminder(reminder)
            if (insertedId > 0) {
                val scheduledReminder = reminder.copy(id = insertedId)
                ReminderScheduler.scheduleReminder(context, scheduledReminder)
            }
            insertedId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create reminder", e)
            -1L
        }
    }

    suspend fun updateReminder(
        id: Long,
        title: String,
        cropName: String?,
        reminderType: ReminderType,
        reminderTimestamp: Long,
        formattedDate: String,
        formattedTime: String,
        notes: String?,
        weatherPrecaution: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserId()
            val reminder = FarmingReminder(
                id = id,
                userId = userId,
                title = title.trim(),
                cropName = cropName?.trim()?.ifBlank { null },
                reminderType = reminderType,
                reminderTimestamp = reminderTimestamp,
                formattedDate = formattedDate,
                formattedTime = formattedTime,
                notes = notes?.trim()?.ifBlank { null },
                weatherPrecaution = weatherPrecaution?.trim()?.ifBlank { null },
                isCompleted = false
            )
            val success = dbHelper.updateReminder(reminder)
            if (success) {
                ReminderScheduler.cancelReminder(context, id)
                ReminderScheduler.scheduleReminder(context, reminder)
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update reminder", e)
            false
        }
    }

    suspend fun toggleComplete(reminder: FarmingReminder, isCompleted: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserId()
            val success = dbHelper.setReminderCompleted(reminder.id, userId, isCompleted)
            if (success) {
                if (isCompleted) {
                    ReminderScheduler.cancelReminder(context, reminder.id)
                } else {
                    ReminderScheduler.scheduleReminder(context, reminder.copy(isCompleted = false))
                }
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle completion for reminder ${reminder.id}", e)
            false
        }
    }

    suspend fun deleteReminder(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserId()
            val success = dbHelper.deleteReminder(id, userId)
            if (success) {
                ReminderScheduler.cancelReminder(context, id)
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete reminder $id", e)
            false
        }
    }

    suspend fun getAllReminders(): List<FarmingReminder> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        dbHelper.getAllReminders(userId)
    }

    suspend fun getTodayReminders(): List<FarmingReminder> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        dbHelper.getTodayReminders(userId)
    }

    suspend fun getUpcomingReminders(): List<FarmingReminder> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        dbHelper.getUpcomingReminders(userId)
    }

    suspend fun getCompletedReminders(): List<FarmingReminder> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        dbHelper.getCompletedReminders(userId)
    }

    suspend fun getTodayPendingCount(): Int = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        dbHelper.getTodayPendingCount(userId)
    }
}
