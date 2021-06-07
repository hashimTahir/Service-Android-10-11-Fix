package com.hashim.overlayserviceapp

data class LockedAppEntity(val packageName: String) {
    fun parsePackageName(): String {
        return packageName.substring(0, packageName.indexOf("/"))
    }
}