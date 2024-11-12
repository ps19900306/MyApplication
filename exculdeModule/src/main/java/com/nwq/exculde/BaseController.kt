package com.nwq.exculde

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import com.nwq.imgtake.ImgTake
import kotlinx.coroutines.delay

class BaseController(
    val acService: AccessibilityService,
) {


    private val imgTake = ImgTake.imgTake
    private val takeScreenIn = 4000L

    /***
     * 这一块是截图的核心逻辑
     */
    protected suspend fun takeScreen(delayTime: Long = 0): Bitmap {
        if (delayTime > 0) {
            delay(delayTime)
        }
        var bitmap: Bitmap? = null
        do {
            bitmap = imgTake.takeScreenImg()
            if (bitmap == null) {
                delay(takeScreenIn)
            }
        } while (bitmap == null)
        return bitmap
    }


}