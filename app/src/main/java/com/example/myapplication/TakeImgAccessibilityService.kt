package com.example.myapplication

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.nwq.callback.CallBack
import com.nwq.callback.RequestParameter
import com.nwq.loguitls.L
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TakeImgAccessibilityService : AccessibilityService(), RequestParameter<Bitmap> {

    companion object {
        var takeImgTools: RequestParameter<Bitmap>? = null
    }

    override fun onServiceConnected() {
        L.i("TakeImgAccessibilityService", "onServiceConnected")
        takeImgTools = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {
        L.i("TakeImgAccessibilityService", "onInterrupt")
        takeImgTools = null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun onRequestParameter(): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            takeScreenshot(
                Display.DEFAULT_DISPLAY,
                mainExecutor,
                object : AccessibilityService.TakeScreenshotCallback {
                    override fun onSuccess(screenshotResult: AccessibilityService.ScreenshotResult) {
                        val bitmap = Bitmap.wrapHardwareBuffer(
                            screenshotResult.hardwareBuffer, screenshotResult.colorSpace
                        )
                        val srcBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                        bitmap?.recycle()
                        screenshotResult.hardwareBuffer.close()

                        continuation.resume(srcBitmap)
                    }

                    override fun onFailure(i: Int) {
                        continuation.resumeWithException(Exception("Screenshot failed with error code: $i"))
                    }
                }
            )

//            continuation.invokeOnCancellation {
//                // Handle cancellation if needed
//            }
        }
    }


}