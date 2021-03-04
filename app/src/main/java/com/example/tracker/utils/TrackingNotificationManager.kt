package com.example.tracker.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.tracker.R
import com.example.tracker.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingNotificationManager @Inject constructor(@ApplicationContext val context: Context) {

    fun getNotificationBitmap(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.location)
    }

    fun getNotification(): Notification {
        return NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setLargeIcon(getNotificationBitmap())
            .setOngoing(true)
            .setContentTitle("TRACKING")
            .setContentIntent(getPendingIntent()).build()
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).also {
                it.action = ACTION_SHOW_MAP_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}