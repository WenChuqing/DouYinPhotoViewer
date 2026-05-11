package com.douyin.photoviewer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.douyin.photoviewer.model.MediaItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 媒体信息显示组件
 * 显示文件名、日期等信息
 */
@Composable
fun MediaInfo(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    // 格式化日期
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val dateText = dateFormat.format(Date(mediaItem.dateAdded * 1000))
    
    // 格式化文件大小
    val sizeText = formatFileSize(mediaItem.size)
    
    // 媒体类型标签
    val mediaType = when (mediaItem) {
        is MediaItem.Image -> "图片"
        is MediaItem.Video -> "视频"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xCC000000))
            .padding(16.dp)
    ) {
        // 文件名
        Text(
            text = mediaItem.displayName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 详细信息
        Text(
            text = "$mediaType · $sizeText · $dateText",
            color = Color(0xFFCCCCCC),
            fontSize = 12.sp
        )
        
        // 视频显示时长
        if (mediaItem is MediaItem.Video) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "时长: ${formatDuration(mediaItem.duration)}",
                color = Color(0xFFCCCCCC),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * 格式化文件大小
 */
private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}

/**
 * 格式化视频时长
 */
private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    val hours = durationMs / (1000 * 60 * 60)
    
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

/**
 * 记住日期格式化器
 */
@Composable
private fun <T> remember(block: () -> T): T {
    return androidx.compose.runtime.remember { block() }
}
