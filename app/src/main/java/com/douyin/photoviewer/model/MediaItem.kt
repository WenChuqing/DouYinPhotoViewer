package com.douyin.photoviewer.model

import android.net.Uri

/**
 * 媒体文件数据模型
 * 封装图片和视频的共同属性
 */
sealed class MediaItem {
    abstract val id: Long
    abstract val uri: Uri
    abstract val displayName: String
    abstract val dateAdded: Long
    abstract val mimeType: String?
    abstract val size: Long

    /**
     * 图片类型媒体
     */
    data class Image(
        override val id: Long,
        override val uri: Uri,
        override val displayName: String,
        override val dateAdded: Long,
        override val mimeType: String?,
        override val size: Long,
        val width: Int = 0,
        val height: Int = 0
    ) : MediaItem()

    /**
     * 视频类型媒体
     */
    data class Video(
        override val id: Long,
        override val uri: Uri,
        override val displayName: String,
        override val dateAdded: Long,
        override val mimeType: String?,
        override val size: Long,
        val duration: Long = 0,
        val width: Int = 0,
        val height: Int = 0
    ) : MediaItem()
}
