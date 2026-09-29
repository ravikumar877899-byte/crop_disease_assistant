package com.example.aicropcare.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.aicropcare.data.database.ReminderDatabaseHelper
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.data.models.ReminderType
import com.example.aicropcare.notifications.ReminderNotificationHelper

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CROP_NAME = "extra_crop_name"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_NOTES = "extra_notes"
        const val EXTRA_WEATHER = "extra_weather"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        val userId = intent.getIntExtra(EXTRA_USER_ID, -1)

        Log.d(TAG, "Alarm received for reminder ID: $reminderId, user: $userId")

        if (reminderId <= 0) return

        try {
            val dbHelper = ReminderDatabaseHelper(context)
            val dbReminder = if (userId > 0) dbHelper.getReminderById(reminderId, userId) else null

            if (dbReminder != null) {
                if (!dbReminder.isCompleted) {
                    ReminderNotificationHelper.showReminderNotification(context, dbReminder)
                } else {
                    Log.d(TAG, "Reminder $reminderId already completed, skipping notification.")
                }
            } else {
                // Fallback using extras
                val title = intent.getStringExtra(EXTRA_TITLE) ?: return
                val crop = intent.getStringExtra(EXTRA_CROP_NAME)
                val type = ReminderType.fromString(intent.getStringExtra(EXTRA_TYPE))
                val notes = intent.getStringExtra(EXTRA_NOTES)
                val weather = intent.getStringExtra(EXTRA_WEATHER)
                val date = intent.getStringExtra(EXTRA_DATE) ?: ""
                val time = intent.getStringExtra(EXTRA_TIME) ?: ""
                val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())

                val fallbackReminder = FarmingReminder(
                    id = reminderId,
                    userId = userId,
                    title = title,
                    cropName = crop,
                    reminderType = type,
                    reminderTimestamp = timestamp,
                    formattedDate = date,
                    formattedTime = time,
                    notes = notes,
                    weatherPrecaution = weather,
                    isCompleted = false
                )
                ReminderNotificationHelper.showReminderNotification(context, fallbackReminder)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling reminder broadcast", e)
        }
    }
}
