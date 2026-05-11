package com.douyin.photoviewer.ui.components

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.douyin.photoviewer.player.VideoPlayerManager

/**
 * 视频播放组件
 * 使用 ExoPlayer 播放视频
 * 支持自动播放和生命周期管理
 */
@Composable
fun VideoItem(
    uri: Uri,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerManager = remember { VideoPlayerManager.getInstance(context) }
    
    // 创建 PlayerView
    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = false // 隐藏默认控制栏
            resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
            setKeepContentOnPlayerReset(true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // 绑定播放器并控制播放状态
        LaunchedEffect(uri, isVisible) {
            if (isVisible) {
                // 页面可见，绑定并播放
                playerManager.bindPlayerView(playerView, uri)
                playerManager.play()
            } else {
                // 页面不可见，暂停
                playerManager.pause()
            }
        }

        // 显示 PlayerView
        AndroidView(
            factory = { playerView },
            modifier = Modifier.fillMaxSize()
        )

        // 加载中显示进度条
        if (!playerManager.isPlaying.value && isVisible) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF25F4EE),
                strokeWidth = 3.dp
            )
        }
    }

    // 组件销毁时清理资源
    DisposableEffect(Unit) {
        onDispose {
            // 只是解绑，不释放播放器
            if (!isVisible) {
                playerManager.pause()
            }
        }
    }
}
