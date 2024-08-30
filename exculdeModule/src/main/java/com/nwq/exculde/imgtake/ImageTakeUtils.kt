package com.nwq.exculde.imgtake

import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK

/**
 * 图像捕获工具类，提供屏幕捕获和截图功能
 */
object ImageTakeUtils {

    // 最大图像数量
    private val maxImages = 10
    // 图像阅读器，用于从虚拟显示器接收图像帧
    private var mImageReader: ImageReader? = null
    // 虚拟显示器，用于捕获屏幕内容
    private var mVirtualDisplay: VirtualDisplay? = null
    // 媒体投影，用于投影屏幕内容
    private var mMediaProjection: MediaProjection? = null
    // 显示标志，定义虚拟显示器的特性
    private const val DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR

    // 已捕获图像计数
    private var count = 0
    // 屏幕宽度
    private var width = 0
    // 屏幕高度
    private var height = 0
    // 屏幕密度（每英寸点数），这里简化为常量，实际应根据设备情况调整
    private val dpi = 1
    // 处理回调的Handler
    private var mHandler: Handler? = null
    // 媒体投影管理器，用于管理媒体投影服务
    private var mMediaProjectionManager: MediaProjectionManager? = null

    /**
     * 获取下一帧图像
     *
     * @return 下一帧Image对象，如果不可用则返回null
     */
    fun acquireNextImage(): Image? {
        // 如果屏幕尺寸未设置，则返回null
        if (width == 0 || height == 0)
            return null
        // 如果已达到最大图像数量，重置并释放资源
        if (count >= maxImages) {
            resetImageReader()
        }
        // 尝试获取下一帧图像
        val image = mImageReader?.acquireNextImage()
        count++
        return image
    }

    /**
     * 初始化工具类，请求媒体投影权限
     *
     * @param activity 启动权限请求的活动
     */
    public fun init(activity: AppCompatActivity) {
        // 获取媒体投影管理器服务
        mMediaProjectionManager =
            activity.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        // 注册活动结果回调，用于处理权限请求结果
        val requestDataLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // 启动记录并设置相关参数
                    result.data?.let {
                        startRecord(
                            mMediaProjectionManager!!, result.resultCode, it,
                            Handler(Looper.getMainLooper()), activity.resources.displayMetrics
                        )
                    }
                }
            }
        // 创建屏幕捕获的意图并启动权限请求流程
        mMediaProjectionManager?.createScreenCaptureIntent()?.let {
            requestDataLauncher.launch(it)
        }
    }

    /**
     * 开始记录屏幕
     *
     * @param mediaProjectionManager 媒体投影管理器，用于创建媒体投影
     * @param resultCode              请求结果码
     * @param data                    包含用户授权信息的Intent
     * @param handler                 处理回调的Handler
     * @param metrics                 屏幕显示度量，用于设置虚拟显示器的尺寸和密度
     */
    fun startRecord(
        mediaProjectionManager: MediaProjectionManager,
        resultCode: Int,
        data: Intent,
        handler: Handler?,
        metrics: DisplayMetrics
    ) {
        // 创建 ImageReader 对象，用于从虚拟显示器中获取图像帧
        mMediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        // 设置屏幕宽度和高度
        width = metrics.widthPixels
        height = metrics.heightPixels
        // 设置Handler用于主线程回调
        mHandler = handler
        // 创建新的ImageReader和虚拟显示器
        createNewImageReader()
    }

    /**
     * 创建新的ImageReader对象，并设置虚拟显示器
     */
    private fun createNewImageReader() {
        // 创建新的ImageReader实例
        mImageReader =
            ImageReader.newInstance(width, height, 0x1, maxImages)
        // 创建虚拟显示器，指定虚拟显示器的名称、宽度、高度、dpi 等参数
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width, height, dpi,
            DISPLAY_FLAGS,
            mImageReader!!.surface, object : VirtualDisplay.Callback() {
                // 当虚拟显示器暂停时回调
                override fun onPaused() {

                }

                // 当虚拟显示器从暂停状态恢复时回调
                override fun onResumed() {

                }

                // 当虚拟显示器被系统停止时回调，此时不再接收帧且不会恢复
                override fun onStopped() {

                }
            }, mHandler
        )
    }

    /**
     * 重置ImageReader，用于在达到最大图像数量后重新初始化
     */
    private fun resetImageReader() {
        // 释放现有资源并创建新的ImageReader
        releaseResources()
        createNewImageReader()
        count = 0
    }

    /**
     * 释放所有资源，包括ImageReader和VirtualDisplay
     */
    private fun releaseResources() {
        // 关闭并重置ImageReader
        mImageReader?.close()
        mImageReader = null
        // 释放并重置VirtualDisplay
        mVirtualDisplay?.release()
        mVirtualDisplay = null
    }

    /**
     * 停止屏幕记录，释放资源并停止媒体投影
     */
    fun stopRecord() {
        // 释放所有资源
        releaseResources()
        // 停止媒体投影
        mMediaProjection?.stop()
        mMediaProjection = null
    }
}
