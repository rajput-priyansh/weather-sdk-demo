package com.vibs.weatherdemosdk.base


interface PermissionCallback {
    fun onPermissionGranted(grantedPermissions: ArrayList<String>)
    fun onPermissionDenied(deniedPermissions: ArrayList<String>)
    fun onPermissionBlocked(blockedPermissions: ArrayList<String>)
}