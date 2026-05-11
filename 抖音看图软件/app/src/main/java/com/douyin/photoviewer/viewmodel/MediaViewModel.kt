package com.douyin.photoviewer.viewmodel

import android.app.Application
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.douyin.photoviewer.data.MediaRepository
import com.douyin.photoviewer.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

/**
 * 媒体浏览 ViewModel
 * 管理媒体列表状态、已浏览集合、播放状态等
 */
class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)

    // 媒体列表状态
    private val _mediaList = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaList: StateFlow<List<MediaItem>> = _mediaList.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 当前页面索引
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // 已浏览的媒体ID集合，确保不重复浏览
    private val viewedMediaIds = Collections.synchronizedSet(mutableSetOf<Long>())

    // 删除操作的 IntentSender (Android 11+)
    private val _deleteIntentSender = MutableStateFlow<IntentSender?>(null)
    val deleteIntentSender: StateFlow<IntentSender?> = _deleteIntentSender.asStateFlow()

    // 删除状态
    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult.asStateFlow()

    // 需要显示删除确认提示
    private val _showDeleteHint = MutableStateFlow(false)
    val showDeleteHint: StateFlow<Boolean> = _showDeleteHint.asStateFlow()

    // 记录上次点击删除时间（用于双击检测）
    private var lastDeleteClickTime = 0L

    init {
        loadMedia()
    }

    /**
     * 加载所有媒体文件
     */
    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allMedia = repository.getAllMedia()
                // 过滤掉已浏览的媒体
                val unviewedMedia = allMedia.filter { it.id !in viewedMediaIds }
                _mediaList.value = unviewedMedia.ifEmpty { allMedia }
            } catch (e: Exception) {
                e.printStackTrace()
                _mediaList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新当前页面
     */
    fun updateCurrentPage(page: Int) {
        _currentPage.value = page
        
        // 标记为已浏览
        if (page < _mediaList.value.size) {
            val mediaId = _mediaList.value[page].id
            viewedMediaIds.add(mediaId)
        }
    }

    /**
     * 获取当前显示的媒体项
     */
    fun getCurrentMedia(): MediaItem? {
        val list = _mediaList.value
        val page = _currentPage.value
        return if (list.isNotEmpty() && page < list.size) {
            list[page]
        } else {
            null
        }
    }

    /**
     * 处理删除按钮点击（双击删除）
     */
    fun onDeleteClick() {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastDeleteClickTime < 500) {
            // 500ms 内的第二次点击，执行删除
            performDelete()
            _showDeleteHint.value = false
        } else {
            // 第一次点击，显示提示
            _showDeleteHint.value = true
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)
                _showDeleteHint.value = false
            }
        }
        
        lastDeleteClickTime = currentTime
    }

    /**
     * 执行删除操作
     */
    private fun performDelete() {
        val currentMedia = getCurrentMedia() ?: return

        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ 使用系统回收站
                try {
                    withContext(Dispatchers.IO) {
                        val pendingIntent = MediaStore.createTrashRequest(
                            getApplication<Application>().contentResolver,
                            listOf(currentMedia.uri),
                            true
                        )
                        _deleteIntentSender.value = pendingIntent.intentSender
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _deleteResult.value = false
                }
            } else {
                // Android 11 以下直接删除
                val success = repository.trashMedia(currentMedia)
                _deleteResult.value = success
                if (success) {
                    // 从列表中移除
                    removeCurrentMedia()
                }
            }
        }
    }

    /**
     * 处理删除结果回调
     */
    fun onDeleteResult(success: Boolean) {
        _deleteIntentSender.value = null
        _deleteResult.value = success
        
        if (success) {
            removeCurrentMedia()
        }
        
        // 重置状态
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _deleteResult.value = null
        }
    }

    /**
     * 从列表中移除当前媒体项
     */
    private fun removeCurrentMedia() {
        val currentIndex = _currentPage.value
        val currentList = _mediaList.value.toMutableList()
        
        if (currentIndex < currentList.size) {
            currentList.removeAt(currentIndex)
            _mediaList.value = currentList
            
            // 如果删除的是最后一项，调整索引
            if (currentIndex >= currentList.size && currentList.isNotEmpty()) {
                _currentPage.value = currentList.size - 1
            }
        }
    }

    /**
     * 清除删除 IntentSender
     */
    fun clearDeleteIntent() {
        _deleteIntentSender.value = null
    }

    /**
     * 是否已浏览全部媒体
     */
    fun hasViewedAll(): Boolean {
        return viewedMediaIds.size >= _mediaList.value.size
    }

    /**
     * 重置浏览记录
     */
    fun resetViewedHistory() {
        viewedMediaIds.clear()
        loadMedia()
    }
}
