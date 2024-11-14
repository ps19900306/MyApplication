package com.nwq.imgtake

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import kotlinx.coroutines.delay
import com.nwq.baseutils.isLandscape
import com.nwq.baseutils.isVertical
import org.opencv.core.Mat

/**
 * 截图接口
 * 这里只负责获取图片 不做过于复杂的事情判断
 * 不做扩展 不加方法
 */
abstract class ImgTake {

    companion object {
        //这个必须初始化ImgTake的一个子类 并赋值
        lateinit var imgTake: ImgTake
        const val TAKE_SCREEN_DELAY = 4000L
    }


    abstract suspend fun takeScreenImg(): Bitmap?

    //每次获取新的图片需要清楚掉原来的
    protected fun clearLastBitMapCache() {
        lastMat = null
        hsvMatMap.clear()
    }

    abstract suspend fun getLastImg(): Bitmap?
    private var hsvMatMap = HashMap<CoordinateArea, Mat>()
    private var lastMat: Mat? = null


    suspend fun getMat(area: CoordinateArea): Mat? {
        return hsvMatMap[area] ?: run {
            if (lastMat == null) {
                lastMat = getLastImg()?.let { lastBit ->
                    MatUtils.bitmapToHsvMat(lastBit)
                }
            }
            val srcMat = lastMat?.let {
                MatUtils.cropMat(it, area)
            }
            if (srcMat != null) {
                hsvMatMap[area] = srcMat
            }
            srcMat
        }
    }

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