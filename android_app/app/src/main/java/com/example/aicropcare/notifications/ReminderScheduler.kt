package com.example.aicropcare.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.receiver.ReminderBroadcastReceiver

object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    fun scheduleReminder(context: Context, reminder: FarmingReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Check if reminder is in the past
        val triggerTime = reminder.reminderTimestamp
        if (triggerTime <= System.currentTimeMillis()) {
            Log.d(TAG, "Reminder time is in the past, skipping schedule.")
            return
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderBroadcastReceiver.EXTRA_USER_ID, reminder.userId)
            putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, reminder.title)
            putExtra(ReminderBroadcastReceiver.EXTRA_CROP_NAME, reminder.cropName)
            putExtra(ReminderBroadcastReceiver.EXTRA_TYPE, reminder.reminderType.name)
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTES, reminder.notes)
            putExtra(ReminderBroadcastReceiver.EXTRA_WEATHER, reminder.weatherPrecaution)
            putExtra(ReminderBroadcastReceiver.EXTRA_DATE, reminder.formattedDate)
            putExtra(ReminderBroadcastReceiver.EXTRA_TIME, reminder.formattedTime)
            putExtra(ReminderBroadcastReceiver.EXTRA_TIMESTAMP, reminder.reminderTimestamp)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for reminder ID: ${reminder.id} at $triggerTime")
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission not granted, scheduling standard alarm", e)
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to schedule alarm", ex)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm", e)
        }
    }

    fun cancelReminder(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm for reminder ID: $reminderId")
        }
    }
}
