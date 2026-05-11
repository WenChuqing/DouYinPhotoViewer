package com.douyin.photoviewer.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.douyin.photoviewer.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 媒体仓库类
 * 负责从 MediaStore 读取本地媒体文件
 */
class MediaRepository(private val context: Context) {

    /**
     * 获取所有图片和视频文件
     * 按添加时间倒序排列（最新的在前）
     */
    suspend fun getAllMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaItem>()

        // 获取图片
        mediaList.addAll(getImages())
        
        // 获取视频
        mediaList.addAll(getVideos())

        // 按时间倒序排序（最新的在前）
        mediaList.sortByDescending { it.dateAdded }
        
        mediaList
    }

    /**
     * 获取所有图片
     */
    private suspend fun getImages(): List<MediaItem.Image> = withContext(Dispatchers.IO) {
        val images = mutableListOf<MediaItem.Image>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                images.add(
                    MediaItem.Image(
                        id = id,
                        uri = uri,
                        displayName = cursor.getString(displayNameColumn),
                        dateAdded = cursor.getLong(dateAddedColumn),
                        mimeType = cursor.getString(mimeTypeColumn),
                        size = cursor.getLong(sizeColumn),
                        width = if (widthColumn >= 0) cursor.getInt(widthColumn) else 0,
                        height = if (heightColumn >= 0) cursor.getInt(heightColumn) else 0
                    )
                )
            }
        }
        
        images
    }

    /**
     * 获取所有视频
     */
    private suspend fun getVideos(): List<MediaItem.Video> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<MediaItem.Video>()
        
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                videos.add(
                    MediaItem.Video(
                        id = id,
                        uri = uri,
                        displayName = cursor.getString(displayNameColumn),
                        dateAdded = cursor.getLong(dateAddedColumn),
                        mimeType = cursor.getString(mimeTypeColumn),
                        size = cursor.getLong(sizeColumn),
                        duration = if (durationColumn >= 0) cursor.getLong(durationColumn) else 0,
                        width = if (widthColumn >= 0) cursor.getInt(widthColumn) else 0,
                        height = if (heightColumn >= 0) cursor.getInt(heightColumn) else 0
                    )
                )
            }
        }
        
        videos
    }

    /**
     * 将文件移入回收站（Android 11+）
     * @return 是否成功发起删除请求
     */
    suspend fun trashMedia(mediaItem: MediaItem): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val uris = listOf(mediaItem.uri)
                val pendingIntent = MediaStore.createTrashRequest(
                    context.contentResolver,
                    uris,
                    true
                )
                
                // 返回 true 表示需要启动 IntentSender
                // 实际的 Intent 发送需要在 Activity 中处理
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            // Android 11 以下直接删除
            try {
                context.contentResolver.delete(mediaItem.uri, null, null)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
