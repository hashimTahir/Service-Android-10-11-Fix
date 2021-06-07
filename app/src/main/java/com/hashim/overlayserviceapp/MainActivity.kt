package com.hashim.overlayserviceapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hashim.overlayserviceapp.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var hActivityMainBinding: ActivityMainBinding
    var hAppList = mutableListOf<AppData>()
    lateinit var hSharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hActivityMainBinding.root)

        hSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        UsageAccessPermissionDialog.newInstance().show(supportFragmentManager, "")

        startForegroundService(Intent(this, AppLockerService::class.java))
        hGetAppsList()
        hInitAdapter()

    }

    private fun hInitAdapter() {
        val hAppAdapter = AppsAdapter(this) {
            val string = hSharedPreferences.edit().putString("AppName", it.appName).commit()
            Timber.d("String $string")
        }.also {
            it.setAppDataList(hAppList)
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


        hAppList = appDataList
    }
}
