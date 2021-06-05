package com.hashim.overlayserviceapp

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AppLockAccessibiltyService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}