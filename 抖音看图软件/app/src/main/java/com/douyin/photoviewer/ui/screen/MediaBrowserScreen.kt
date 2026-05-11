package com.douyin.photoviewer.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.douyin.photoviewer.model.MediaItem
import com.douyin.photoviewer.ui.components.DeleteButton
import com.douyin.photoviewer.ui.components.ImageItem
import com.douyin.photoviewer.ui.components.MediaInfo
import com.douyin.photoviewer.ui.components.VideoItem
import com.douyin.photoviewer.viewmodel.MediaViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 媒体浏览主屏幕
 * 使用 VerticalPager 实现抖音式上下滑动浏览
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaBrowserScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showDeleteHint by viewModel.showDeleteHint.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            // 加载中
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF25F4EE),
                        strokeWidth = 3.dp
                    )
                }
            }
            
            // 没有媒体文件
            mediaList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "未找到媒体文件\n请授予存储权限",
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            // 显示媒体列表
            else -> {
                val pagerState = rememberPagerState()

                // 监听页面变化，更新 ViewModel 状态
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .distinctUntilChanged()
                        .collect { page ->
                            viewModel.updateCurrentPage(page)
                        }
                }

                // VerticalPager - 垂直滑动浏览
                VerticalPager(
                    count = mediaList.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val mediaItem = mediaList[page]
                    val isCurrentPage = page == pagerState.currentPage

                    Box(modifier = Modifier.fillMaxSize()) {
                        // 根据媒体类型显示图片或视频
                        when (mediaItem) {
                            is MediaItem.Image -> {
                                ImageItem(
                                    uri = mediaItem.uri,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            is MediaItem.Video -> {
                                VideoItem(
                                    uri = mediaItem.uri,
                                    isVisible = isCurrentPage,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // 底部媒体信息
                        MediaInfo(
                            mediaItem = mediaItem,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        )

                        // 右侧删除按钮
                        DeleteButton(
                            onClick = { viewModel.onDeleteClick() },
                            showHint = showDeleteHint && isCurrentPage,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                    }
                }

                // 删除结果提示
                AnimatedVisibility(
                    visible = deleteResult != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    val message = if (deleteResult == true) "已移入回收站" else "删除失败"
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xCC000000))
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // 页面指示器
                Text(
                    text = "${pagerState.currentPage + 1} / ${mediaList.size}",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp)
                        .background(Color(0x80000000))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
