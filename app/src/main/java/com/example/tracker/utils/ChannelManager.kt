package com.example.tracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.os.Build
import androidx.annotation.RequiresApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelManager @Inject constructor(val notificationManager: NotificationManager) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel =
            NotificationChannel(TRACKING_CHANNEL_ID, TRACKING_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}