package com.douyin.photoviewer.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * 图片显示组件
 * 使用 Glide Compose 加载图片
 * 支持三级缓存策略
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageItem(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                // 加载中显示进度条
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF25F4EE),
                        strokeWidth = 3.dp
                    )
                }
            },
            failure = {
                // 加载失败显示占位
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "加载失败",
                        color = Color.White
                    )
                }
            }
        ) { requestBuilder ->
            requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 三级缓存：磁盘缓存
                .skipMemoryCache(false) // 内存缓存
                .centerInside()
        }
    }
}
