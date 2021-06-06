package com.hashim.overlayserviceapp

import android.app.AppOpsManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import timber.log.Timber

class AppLockerService : Service() {

    private val hBinder = Hbinder()

    val hServiceNotificationManager = ServiceNotificationManager(this)

    private val hJob = SupervisorJob()
    private val hScope = CoroutineScope(Dispatchers.IO + hJob)

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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @InternalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        val notification = hServiceNotificationManager.createNotification()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        if (checkUsageAccessPermission(this)) {
            showPermissionNeedNotification()
        }
        try {
            hScope.launch {
                while (NonCancellable.isActive) {
                    observeForegroundApplication()
                    delay(100L)
                }
            }
            Timber.d("Service Started")
        } catch (e: Exception) {
        }


    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun observeForegroundApplication() {
        Timber.d("Observefor")

        var usageEvent: UsageEvents.Event? = null

        val mUsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time)
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                usageEvent = event
            }
        }
        usageEvent?.let {
            Timber.d("Class name ${it.className} and packagename ${it.packageName}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SErvice destroyed")
    }

    inner class Hbinder : Binder() {
        fun hGetService(): AppLockerService = this@AppLockerService
    }


    private fun showPermissionNeedNotification() {
        val notification = hServiceNotificationManager.createPermissionNeedNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED, notification)
    }


    fun checkUsageAccessPermission(context: Context): Boolean {

        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0

            mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid, applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED

        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }
}

