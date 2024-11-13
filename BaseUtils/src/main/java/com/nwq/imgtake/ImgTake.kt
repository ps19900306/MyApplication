package com.nwq.imgtake

import android.graphics.Bitmap
import kotlinx.coroutines.delay
import com.nwq.baseutils.isLandscape
import com.nwq.baseutils.isVertical

/**
 * 截图接口
 * 这里只负责获取图片 不做过于复杂的事情判断
 * 不做扩展 不加方法
 */
interface ImgTake {

    companion object {
        //这个必须初始化ImgTake的一个子类 并赋值
        lateinit var imgTake: ImgTake
        const val TAKE_SCREEN_DELAY = 4000L
    }

    suspend fun takeScreenImg(): Bitmap?

    suspend fun getLastImg(): Bitmap?

    //获取横屏截图
    suspend fun taskScreenL(delayTime: Long = 0): Boolean {
        if (delayTime > 0) {
            delay(delayTime)
        }
        var bitmap: Bitmap? = null
        do {
            bitmap = takeScreenImg()
            if (bitmap == null) {
                delay(TAKE_SCREEN_DELAY)
            }
        } while (bitmap == null)
        return bitmap.isLandscape()
    }

    //获取竖屏截图
    suspend fun taskScreenV(delayTime: Long = 0): Boolean {
        if (delayTime > 0) {
            delay(delayTime)
        }
        var bitmap: Bitmap? = null
        do {
            bitmap = takeScreenImg()
            if (bitmap == null) {
                delay(TAKE_SCREEN_DELAY)
            }
        } while (bitmap == null)
        return bitmap.isVertical()
    }
}