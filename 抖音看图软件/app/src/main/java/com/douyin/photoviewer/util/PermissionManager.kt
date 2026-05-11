package com.douyin.photoviewer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * 权限管理工具类
 * 处理 Android 不同版本的存储权限申请
 */
object PermissionManager {

    /**
     * 获取需要申请的存储权限列表
     */
    fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33)
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10-12 (API 29-32)
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                // Android 9 及以下 (API 28-)
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    /**
     * 检查是否已获得所有必要权限
     */
    fun hasAllPermissions(context: Context): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 检查权限请求结果
     */
    fun checkPermissionResult(permissions: Array<String>, grantResults: IntArray): Boolean {
        if (permissions.isEmpty() || grantResults.isEmpty()) {
            return false
        }
        
        return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }

    /**
     * 是否需要显示权限说明
     */
    fun shouldShowRationale(): Boolean {
        // 可以根据业务逻辑判断是否需要显示权限说明
        return true
    }
}
