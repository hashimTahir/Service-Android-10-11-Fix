package com.hashim.overlayserviceapp

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hashim.overlayserviceapp.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var hActivityMainBinding: ActivityMainBinding
    var hList = mutableListOf<AppData>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)

        startForegroundService(Intent(this, AppLockerService::class.java))
        hGetAppsList()
        hInitAdapter()

    }

    private fun hInitAdapter() {
        val hAppAdapter = AppsAdapter().also {
            it.setAppDataList(hList)
        }
        hActivityMainBinding.hAppRv.adapter = hAppAdapter
    }

    fun hGetAppsList() {

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList: List<ResolveInfo> =
            packageManager.queryIntentActivities(mainIntent, 0)


        val appDataList: ArrayList<AppData> = arrayListOf()

        resolveInfoList.forEach { resolveInfo ->
            with(resolveInfo) {
                try {
                    if (activityInfo.packageName != packageName) {
                        val mainActivityName =
                            activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1)
                        val appData = AppData(
                            appName = loadLabel(packageManager) as String,
                            packageName = "${activityInfo.packageName}/$mainActivityName",
                            appIconDrawable = loadIcon(packageManager)
                        )
                        appDataList.add(appData)
                    }
                } catch (e: Exception) {
                }
            }

        }


        hList = appDataList
        Timber.d("Applist size ${appDataList.size}")
    }
}
