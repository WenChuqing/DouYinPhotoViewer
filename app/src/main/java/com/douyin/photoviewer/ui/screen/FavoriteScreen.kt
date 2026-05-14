package com.douyin.photoviewer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.douyin.photoviewer.ui.components.ImageItem
import com.douyin.photoviewer.ui.components.VideoItem

@Composable
fun FavoriteScreen(
    viewModel: MediaViewModel, // 换成你自己的ViewModel名字
    onMediaClick: (Long) -> Unit
) {
    val favoriteMedia = viewModel.getFavoriteMedia()

    if (favoriteMedia.isEmpty()) {
        // 还没有收藏
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "还没有收藏任何内容哦~",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        // 显示收藏的图片和视频
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3列显示
            contentPadding = PaddingValues(4.dp)
        ) {
            items(favoriteMedia.size) { index ->
                val media = favoriteMedia[index]
                
                when (media) {
                    is MediaItem.Image -> {
                        ImageItem(
                            media = media,
                            onClick = { onMediaClick(media.id) }
                        )
                    }
                    is MediaItem.Video -> {
                        VideoItem(
                            media = media,
                            onClick = { onMediaClick(media.id) }
                        )
                    }
                }
            }
        }
    }
}
