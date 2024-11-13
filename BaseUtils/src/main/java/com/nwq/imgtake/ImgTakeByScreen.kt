package com.nwq.imgtake

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.view.Display
import androidx.annotation.RequiresApi
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 图像捕获工具类，提供屏幕捕获和截图功能
 * 基于无障碍服务的
 * 这个在无障碍服务成功时候里面初始化
 */
class ImgTakeByScreen(val acService: AccessibilityService) : ImgTake {


    private var lastImg: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun takeScreenImg(): Bitmap? = suspendCoroutine {
        lastImg?.recycle()
        lastImg = null
        acService.takeScreenshot(
            Display.DEFAULT_DISPLAY,
            acService.mainExecutor,
            object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(screenshotResult: AccessibilityService.ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshotResult.hardwareBuffer, screenshotResult.colorSpace
                    )
                    val screenBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                    bitmap?.recycle()
                    screenshotResult.hardwareBuffer.close()
                    lastImg = screenBitmap
                    it.resume(screenBitmap)

                }

                override fun onFailure(i: Int) {
                    it.resume(null)
                }
            })
    }

    override suspend fun getLastImg(): Bitmap? {
       return lastImg
    }


}