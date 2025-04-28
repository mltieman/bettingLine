package com.example.bettingline

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class GameReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "GameReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val title   = intent.getStringExtra("title")   ?: "Upcoming Game!"
            val message = intent.getStringExtra("message") ?: "Don't miss your game."

            // Intent to open your app when the user taps the notification:
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                // Clears any existing task and starts fresh
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val openAppPI = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, "game_channel")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(openAppPI)
                .setAutoCancel(true)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Missing POST_NOTIFICATIONS permission; skipping notify()")
                return
            }

            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), builder.build())

        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
}
