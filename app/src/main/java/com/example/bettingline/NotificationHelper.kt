package com.example.bettingline

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object NotificationHelper {
    fun scheduleNotification(
        context: Context,
        triggerTimeMillis: Long,
        title: String,
        message: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // fallback
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    PendingIntent.getBroadcast(
                        context,
                        (triggerTimeMillis % Int.MAX_VALUE).toInt(),
                        Intent(context, GameReminderReceiver::class.java).apply {
                            putExtra("title", title)
                            putExtra("message", message)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                return
            }
        }

        val pi = PendingIntent.getBroadcast(
            context,
            (triggerTimeMillis % Int.MAX_VALUE).toInt(),
            Intent(context, GameReminderReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("message", message)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pi)
        } catch (sec: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pi)
        }
    }
}
