package com.hashim.overlayserviceapp

import android.app.Application
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        hInintTimber()
    }


    private fun hInintTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(
                    priority: Int,
                    tag: String?,
                    message: String,
                    t: Throwable?
                ) {
                    super.log(priority, String.format("HashimTimberTags %s", tag), message, t)
                }
            })
        }
    }
}


