package com.example.aicropcare.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.aicropcare.MainActivity
import com.example.aicropcare.R
import com.example.aicropcare.data.models.FarmingReminder

object ReminderNotificationHelper {

    const val CHANNEL_ID = "farming_reminders"
    private const val CHANNEL_NAME = "Farming Reminders"
    private const val CHANNEL_DESC = "Notifications for crop treatments, irrigation, re-scans, and field inspections"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(context: Context, reminder: FarmingReminder) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NAV_DESTINATION", "reminders")
            putExtra("REMINDER_ID", reminder.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val appTitle = context.getString(R.string.app_name)
        val titleText = if (reminder.title.isNotBlank()) "$appTitle - ${reminder.title}" else context.getString(R.string.notification_default_title)
        val contentBody = buildString {
            if (!reminder.cropName.isNullOrBlank()) {
                append("${reminder.cropName}: ")
            }
            if (!reminder.notes.isNullOrBlank()) {
                append(reminder.notes)
            } else if (!reminder.weatherPrecaution.isNullOrBlank()) {
                append(reminder.weatherPrecaution)
            } else {
                append(context.getString(R.string.notification_default_message))
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleText)
            .setContentText(contentBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(reminder.id.toInt(), builder.build())
        } catch (e: SecurityException) {
            android.util.Log.w("ReminderNotification", "Notification permission not granted", e)
        }
    }
}
