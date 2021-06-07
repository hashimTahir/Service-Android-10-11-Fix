package com.hashim.overlayserviceapp

import android.app.AppOpsManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

class AppLockerService : Service() {

    private val hBinder = Hbinder()

    val hServiceNotificationManager = ServiceNotificationManager(this)

    /*For running infinitely*/
    private val hJob = SupervisorJob()
    private val hScope = CoroutineScope(Dispatchers.IO + hJob)

    /*Holder for what is locked or not
    * Should be db in case of real app*/
    lateinit var hSharedPreferences: SharedPreferences

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
        hSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)

        /*Not needed here, jst for testing*/
        if (checkUsageAccessPermission(this)) {
            showPermissionNeedNotification()
        }

        /*Repeat this process every 100millis*/
        try {
            hScope.launch {
                while (NonCancellable.isActive) {
                    hCheckCurrentlyRunningApps()
                    delay(100L)
                }
            }
            Timber.d("Service Started")
        } catch (e: Exception) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun hCheckCurrentlyRunningApps() {

        var usageEvent: UsageEvents.Event? = null

        val mUsageStatsManager = getSystemService(USAGE_STATS_SERVICE)
                as UsageStatsManager
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
            hCheckIfAppIsLockedOrNot(it)
        }
    }

    /*If is one of the locked apps then dont let it open, present the unlocker
    * page, in this case represented by testActivity*/
    private fun hCheckIfAppIsLockedOrNot(it: UsageEvents.Event) {
        val string = hSharedPreferences.getString("AppName", null)


        string?.let { cam ->
            Timber.d("Cam ${cam.lowercase(Locale.getDefault())}")
            Timber.d("Pac ${it.packageName}")
            if (it.packageName.contains(cam.lowercase())) {
                val intent = Intent(this, TestActivity::class.java)
                intent.putExtra("forservice", true)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                Timber.i("Start Activity here")
            }
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

