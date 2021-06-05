package com.hashim.overlayserviceapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import timber.log.Timber

class AppLockerService : Service() {

    private val hBinder = Hbinder()

    val hServiceNotificationManager = ServiceNotificationManager(this)

    companion object {
        private const val NOTIFICATION_ID_APPLOCKER_SERVICE = 1
        private const val NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED = 2
    }

    override fun onBind(intent: Intent?): IBinder {
        return hBinder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = hServiceNotificationManager.createNotification()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val notification = hServiceNotificationManager.createNotification()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)

        try {
            Timber.d("Service Started")
        } catch (e: Exception) {
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SErvice destroyed")
    }

    inner class Hbinder : Binder() {
        fun hGetService(): AppLockerService = this@AppLockerService
    }
}

