# 抖音看图 - 抖音式智能看图软件

基于 Jetpack Compose 构建的 Android 抖音式图片/视频浏览应用，支持上下滑动全屏浏览、视频自动播放、文件删除等功能。

## ✨ 功能特性

### 🎬 抖音式随机浏览
- 上下滑动全屏切换图片和视频
- 使用 Compose VerticalPager 实现懒加载
- 使用 MediaStore 扫描本地媒体
- 维护"已浏览"集合确保内容不重复

### ▶️ 视频自动播放
- 视频进入可视区域自动播放
- 离开可视区域立即暂停
- 集成 ExoPlayer 引擎
- 监听 Pager 页面回调控制播放状态
- 严格管理生命周期防止内存泄漏

### 🗑️ 文件删除与恢复
- 双击删除按钮
- 调用 Android 11+ MediaStore.createTrashRequest()
- 文件安全移入系统"最近删除"文件夹
- 动态申请存储权限

### 🖼️ 媒体内容加载
- 集成 Glide 图片加载库
- 使用 Glide Compose 扩展
- 基于 URI 加载
- 三级缓存策略
- 确保列表滚动流畅

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 1.9.20 | 编程语言 |
| Jetpack Compose | BOM 2023.10.01 | UI 框架 |
| Accompanist Pager | 0.32.0 | 分页组件 |
| Media3 (ExoPlayer) | 1.2.0 | 视频播放 |
| Glide | 4.16.0 | 图片加载 |
| Coroutines | 1.7.3 | 异步编程 |
| MVVM | - | 架构模式 |
| StateFlow | - | 状态管理 |

## 📋 最低要求

- Android 8.0 (API 26) 或更高版本
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17

## 🚀 编译运行

### 1. 环境准备

确保已安装：
- Android Studio
- Android SDK API 34
- Gradle 8.2+

### 2. 导入项目

1. 打开 Android Studio
2. 选择 `File` -> `Open`
3. 选择项目根目录（`抖音看图/`）
4. 等待 Gradle 同步完成

### 3. 连接设备或启动模拟器

- 连接真实 Android 设备（需开启 USB 调试）
- 或启动 Android 模拟器

### 4. 运行应用

点击 Android Studio 工具栏的 Run 按钮（▶️），或使用快捷键：
- macOS: `Ctrl + R`
- Windows/Linux: `Shift + F10`

## 📁 项目结构

```
抖音看图/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/douyin/photoviewer/
│   │       │   ├── MainActivity.kt              # 主 Activity
│   │       │   ├── model/
│   │       │   │   └── MediaItem.kt             # 媒体数据模型
│   │       │   ├── data/
│   │       │   │   └── MediaRepository.kt       # 媒体数据仓库
│   │       │   ├── viewmodel/
│   │       │   │   └── MediaViewModel.kt        # ViewModel
│   │       │   ├── player/
│   │       │   │   └── VideoPlayerManager.kt    # 视频播放器管理
│   │       │   ├── util/
│   │       │   │   └── PermissionManager.kt     # 权限管理
│   │       │   └── ui/
│   │       │       ├── components/
│   │       │       │   ├── ImageItem.kt         # 图片组件
│   │       │       │   ├── VideoItem.kt         # 视频组件
│   │       │       │   ├── DeleteButton.kt      # 删除按钮
│   │       │       │   └── MediaInfo.kt         # 媒体信息
│   │       │       └── screen/
│   │       │           ├── MediaBrowserScreen.kt # 浏览主界面
│   │       │           └── PermissionScreen.kt  # 权限请求界面
│   │       ├── res/
│   │       │   ├── values/
│   │       │   ├── xml/
│   │       │   └── mipmap-*/
│   │       └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## 📱 使用说明

### 基本操作
1. **授予权限**：首次启动需授予存储权限
2. **浏览媒体**：上下滑动切换图片/视频
3. **删除文件**：双击右侧删除按钮，文件移入系统回收站
4. **视频播放**：视频自动播放，滑出屏幕自动暂停

### 权限说明
- Android 13+ (API 33): `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`
- Android 10-12 (API 29-32): `READ_EXTERNAL_STORAGE`
- Android 9 及以下: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`

## 🔧 核心实现要点

### 1. VerticalPager 懒加载
使用 Accompanist 的 VerticalPager 实现垂直滑动，配合 Glide 的图片缓存机制，确保滚动流畅。

### 2. ExoPlayer 生命周期管理
使用单例模式管理 ExoPlayer，确保同一时间只有一个视频在播放，避免内存泄漏。

### 3. MediaStore 媒体扫描
通过 ContentResolver 查询 MediaStore 获取本地图片和视频，支持 Android 各版本。

### 4. Android 11+ 回收站功能
使用 `MediaStore.createTrashRequest()` 将文件安全移入系统回收站，而非直接永久删除。

### 5. 已浏览集合
使用同步 Set 维护已浏览的媒体 ID，确保内容不重复展示，提升用户体验。

## 📄 License

本项目仅供学习和参考使用。

---

**注意**：请确保在真实设备上测试，模拟器可能无法正确扫描媒体文件。
