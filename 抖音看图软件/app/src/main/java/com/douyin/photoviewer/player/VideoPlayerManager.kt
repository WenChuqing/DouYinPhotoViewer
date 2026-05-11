package com.douyin.photoviewer.player

import android.content.Context
import android.net.Uri
import android.view.SurfaceView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 视频播放器管理器
 * 单例模式，管理 ExoPlayer 的生命周期和播放状态
 * 确保同一时间只有一个视频在播放，防止内存泄漏
 */
class VideoPlayerManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: VideoPlayerManager? = null

        fun getInstance(context: Context): VideoPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VideoPlayerManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    // 当前播放器实例
    private var exoPlayer: ExoPlayer? = null

    // 当前播放的视频 URI
    private val _currentUri = MutableStateFlow<Uri?>(null)
    val currentUri: StateFlow<Uri?> = _currentUri.asStateFlow()

    // 播放状态
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // 当前绑定的 PlayerView
    private var currentPlayerView: PlayerView? = null

    /**
     * 初始化播放器
     */
    private fun initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                playWhenReady = false
                repeatMode = Player.REPEAT_MODE_ONE // 循环播放
                addListener(playerListener)
            }
        }
    }

    /**
     * 播放器状态监听器
     */
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                    // 播放器空闲
                }
                Player.STATE_BUFFERING -> {
                    // 缓冲中
                }
                Player.STATE_READY -> {
                    // 准备完成
                }
                Player.STATE_ENDED -> {
                    // 播放结束，自动重播
                    exoPlayer?.seekTo(0)
                    exoPlayer?.play()
                }
            }
        }
    }

    /**
     * 绑定 PlayerView 并准备播放
     * @param playerView 播放器视图
     * @param uri 视频 URI
     */
    fun bindPlayerView(playerView: PlayerView, uri: Uri) {
        initializePlayer()
        
        // 先解绑之前的 PlayerView
        currentPlayerView?.player = null
        
        // 绑定新的 PlayerView
        currentPlayerView = playerView
        playerView.player = exoPlayer
        
        // 如果 URI 不同，设置新的媒体源
        if (_currentUri.value != uri) {
            setMediaItem(uri)
        }
    }

    /**
     * 设置媒体源
     */
    private fun setMediaItem(uri: Uri) {
        exoPlayer?.let { player ->
            val mediaItem = ExoMediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            _currentUri.value = uri
        }
    }

    /**
     * 开始播放
     */
    fun play() {
        exoPlayer?.let { player ->
            if (!player.playWhenReady) {
                player.playWhenReady = true
            }
        }
    }

    /**
     * 暂停播放
     */
    fun pause() {
        exoPlayer?.let { player ->
            if (player.playWhenReady) {
                player.playWhenReady = false
            }
        }
    }

    /**
     * 停止播放并释放资源
     */
    fun stop() {
        exoPlayer?.let { player ->
            player.stop()
            player.clearMediaItems()
        }
        _currentUri.value = null
        _isPlaying.value = false
    }

    /**
     * 释放播放器
     * 应在 Activity/Fragment onDestroy 时调用
     */
    fun release() {
        pause()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        currentPlayerView?.player = null
        currentPlayerView = null
        _currentUri.value = null
        _isPlaying.value = false
    }

    /**
     * 解绑 PlayerView（页面不可见时调用）
     */
    fun unbindPlayerView() {
        pause()
        currentPlayerView?.player = null
        currentPlayerView = null
    }

    /**
     * 检查当前是否正在播放指定 URI
     */
    fun isPlayingUri(uri: Uri): Boolean {
        return _currentUri.value == uri && _isPlaying.value
    }

    /**
     * 切换播放/暂停状态
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }
}
