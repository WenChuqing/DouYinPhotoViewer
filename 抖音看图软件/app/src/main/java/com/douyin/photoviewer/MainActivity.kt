package com.douyin.photoviewer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.douyin.photoviewer.player.VideoPlayerManager
import com.douyin.photoviewer.ui.screen.MediaBrowserScreen
import com.douyin.photoviewer.ui.screen.PermissionScreen
import com.douyin.photoviewer.util.PermissionManager
import com.douyin.photoviewer.viewmodel.MediaViewModel

/**
 * 主 Activity
 * 应用入口点，处理权限请求和 UI 展示
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MediaViewModel by viewModels()
    private lateinit var playerManager: VideoPlayerManager

    // 权限请求 launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // 权限已授予，加载媒体
            viewModel.loadMedia()
        }
    }

    // 删除操作 Intent launcher (Android 11+)
    private val deleteIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // 处理删除结果
        val success = result.resultCode == RESULT_OK
        viewModel.onDeleteResult(success)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化播放器管理器
        playerManager = VideoPlayerManager.getInstance(this)

        setContent {
            // 观察删除 Intent
            val deleteIntentSender by viewModel.deleteIntentSender.collectAsState()
            
            LaunchedEffect(deleteIntentSender) {
                deleteIntentSender?.let { intentSender ->
                    // 启动系统删除确认界面
                    val intentSenderRequest = android.content.IntentSenderRequest
                        .Builder(intentSender)
                        .build()
                    deleteIntentLauncher.launch(intentSenderRequest)
                    viewModel.clearDeleteIntent()
                }
            }

            // 检查权限并显示对应界面
            if (PermissionManager.hasAllPermissions(this)) {
                // 有权限，显示媒体浏览界面
                MediaBrowserScreen(viewModel = viewModel)
            } else {
                // 无权限，显示权限请求界面
                PermissionScreen(
                    onRequestPermission = {
                        requestPermissionLauncher.launch(
                            PermissionManager.getRequiredPermissions()
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 恢复播放
        if (PermissionManager.hasAllPermissions(this)) {
            viewModel.getCurrentMedia()?.let { mediaItem ->
                if (mediaItem is com.douyin.photoviewer.model.MediaItem.Video) {
                    playerManager.play()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 暂停播放
        playerManager.pause()
    }

    override fun onStop() {
        super.onStop()
        // 停止播放
        playerManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放播放器资源
        playerManager.release()
    }
}
