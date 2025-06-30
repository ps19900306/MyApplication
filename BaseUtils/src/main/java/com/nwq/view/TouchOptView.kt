package com.nwq.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseobj.ICoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first


open class TouchOptView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object{
        //不拦截的事件
        private const val NORMAL_TYPE = 0
        //方形区域
        const val RECT_AREA_TYPE = 1
        //圆形区域
        const val CIRCLE_AREA_TYPE = 2
        //单点击
        const val SINGLE_CLICK_TYPE = 3
        //测算距离
        const val MEASURE_DISTANCE_TYPE = 4
    }

    private val _touchCoordinate = MutableStateFlow<ICoordinate?>(null)
    public val nowPoint = MutableStateFlow(CoordinatePoint(0, 0))


    private var starX = 1F
    private var starY = 1F
    private var isFirst = true
    private var nowMode = NORMAL_TYPE
    private var lastTime = 0L
    private val cancelInterval = 2000L

    suspend fun getPoint(): CoordinatePoint {
        _touchCoordinate.value = null
        nowMode = SINGLE_CLICK_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinatePoint
        }.first()
        nowMode = NORMAL_TYPE
        return data as CoordinatePoint
    }

    suspend fun getRectArea(): CoordinateArea {
        _touchCoordinate.value = null
        nowMode = RECT_AREA_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateArea && !it.isRound
        }.first()
        Log.i("TouchOptView", "getRectArea:$data")
        nowMode = NORMAL_TYPE
        return data as CoordinateArea
    }

    suspend fun getCircleArea(): CoordinateArea {
        _touchCoordinate.value = null
        nowMode = CIRCLE_AREA_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateArea && it.isRound
        }.first()
        nowMode = NORMAL_TYPE
        return data as CoordinateArea
    }

    suspend fun measureDistance(): CoordinateLine {
        _touchCoordinate.value = null
        nowMode = MEASURE_DISTANCE_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateLine
        }.first()
        nowMode = NORMAL_TYPE
        return data as CoordinateLine
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (nowMode == NORMAL_TYPE) {
            return super.onTouchEvent(event)
        }
        Log.i("TouchOptView", "nowMode:$nowMode event:$event")
        when (nowMode) {
            SINGLE_CLICK_TYPE -> {
                nowPoint.tryEmit(CoordinatePoint(event.x.toInt(), event.y.toInt()))
                if (isFirst) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        starX = event.x
                        starY = event.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()
                    }
                } else {
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        starX = event.x
                        starY = event.y
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            _touchCoordinate.tryEmit(CoordinatePoint(starX, starY))
                        }
                        isFirst = true
                    }
                }
            }

            RECT_AREA_TYPE, CIRCLE_AREA_TYPE -> {
                if (isFirst) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        starX = event.x
                        starY = event.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()
                    }
                } else {
                    val isCircle = nowMode == CIRCLE_AREA_TYPE
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        val coordinateArea =
                            createCoordinateArea(starX, starY, event.x, event.y, isCircle)
                        // 假设你有一个方法可以绘制这个区域
                        Log.i("TouchOptView", "RECT_AREA_TYPE:$coordinateArea")
                        updatePreviewArea(coordinateArea)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateArea =
                                createCoordinateArea(starX, starY, event.x, event.y, isCircle)
                            _touchCoordinate.tryEmit(coordinateArea)
                        }
                        clearPreviewArea()
                        isFirst = true
                    }
                }
            }

            MEASURE_DISTANCE_TYPE -> {
                if (isFirst) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        starX = event.x
                        starY = event.y
                        isFirst = false
                    }
                } else {
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        val coordinateLine = createCoordinateLine(starX, starY, event.x, event.y)
                        // 假设你有一个方法可以绘制这条线
                        updatePreviewLine(coordinateLine)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateLine =
                                createCoordinateLine(starX, starY, event.x, event.y)
                            _touchCoordinate.tryEmit(coordinateLine)
                        }
                        clearPreviewLine()
                        isFirst = true
                    }
                }
            }
        }
        return true
    }

    // 辅助方法
    protected fun createCoordinateLine(x1: Float, y1: Float, x2: Float, y2: Float): CoordinateLine {
        return if (x1 + y1 > x2 + y2) {
            CoordinateLine(CoordinatePoint(x2, y2), CoordinatePoint(x1, y1))
        } else {
            CoordinateLine(CoordinatePoint(x1, y1), CoordinatePoint(x2, y2))
        }
    }

    protected fun createCoordinateArea(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        b: Boolean = false
    ): CoordinateArea {
        val sX = if (x1 > x2) x2 else x1
        val sY = if (y1 > y2) y2 else y1
        return CoordinateArea(sX, sY, Math.abs(x1 - x2), Math.abs(y1 - y2), b)
    }



    // 预览相关的方法
    protected open fun updatePreviewArea(area: CoordinateArea) {
        Log.i("TouchOptView", "updatePreviewArea:$area")
    }
    protected open fun clearPreviewArea() {}
    protected open fun updatePreviewLine(line: CoordinateLine) {}
    protected open fun clearPreviewLine() {}
}
