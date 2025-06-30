package com.example.myapplication.base

import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.nwq.base.BaseActivity
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.view.PreviewImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 如果需要获取什么操作请看TouchOptModel
 */
abstract class AppTouchActivity<VB : ViewBinding> : BaseActivity<VB>() {

    protected val mTouchOptModel by viewModels<TouchOptModel>()

    /**
     * 这个是进行点和区域选取的
     */
    private var starX = 1F
    private var starY = 1F
    private var isFirst = true
    private var nowMode = TouchOptModel.NORMAL_TYPE
    private var lastTime = 0L
    private val cancelInterval = 2000L

    /**
     * 全屏的
     */
    private lateinit var controller: WindowInsetsControllerCompat


    override fun beforeInitData() {
        super.beforeInitData()
        lifecycleScope.launch {
            mTouchOptModel.touchType.collectLatest {
                when (it) {
                    TouchOptModel.NORMAL_TYPE -> {
                        onNormalView()
                        nowMode = it
                    }

                    TouchOptModel.FULL_SCREEN -> {
                        fullScreen()
                        mTouchOptModel.resetTouchOptFlag()
                    }

                    TouchOptModel.RECT_AREA_TYPE,
                    TouchOptModel.CIRCLE_AREA_TYPE,
                    TouchOptModel.SINGLE_CLICK_TYPE,
                    TouchOptModel.MEASURE_DISTANCE_TYPE,
                        -> {
                        onTouchOptView()
                        nowMode = it
                    }
                }
            }
        }
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val params = window.attributes
        // 设置布局进入刘海区域
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = params
    }


    private val mPreviewView: PreviewImageView?
        get() = getPreviewView()

    open fun getPreviewView(): PreviewImageView? {
        return null
    }

    public open fun fullScreen() {
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    //非区域选取操作的
    open fun onNormalView() {

    }

    //进行区域选取操作
    open fun onTouchOptView() {

    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (nowMode == TouchOptModel.NORMAL_TYPE) {
            return super.onTouchEvent(ev)
        }
        when (nowMode) {
            TouchOptModel.SINGLE_CLICK_TYPE -> {
                mTouchOptModel.nowPoint.tryEmit(CoordinatePoint(ev.x.toInt(), ev.y.toInt()))
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()
                    }
                } else {
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        starX = ev.x
                        starY = ev.y
                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            mTouchOptModel.updateICoordinate(CoordinatePoint(starX, starY))
                        }
                        isFirst = true
                    }
                }
            }

            TouchOptModel.RECT_AREA_TYPE, TouchOptModel.CIRCLE_AREA_TYPE -> {
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()

                    }

                } else {
                    val isCircle = nowMode == TouchOptModel.CIRCLE_AREA_TYPE
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        mPreviewView?.let { previewView ->
                            val coordinateArea =
                                createCoordinateArea(starX, starY, ev.x, ev.y, isCircle)
                            previewView.setArea(coordinateArea)
                        }

                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateArea =
                                createCoordinateArea(starX, starY, ev.x, ev.y, isCircle)
                            mPreviewView?.setArea(coordinateArea)
                            mTouchOptModel.updateICoordinate(coordinateArea)
                        } else {
                            mPreviewView?.clearArea()
                        }
                        isFirst = true
                    }
                }
            }

            TouchOptModel.CIRCLE_AREA_TYPE -> {
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                    }
                } else {
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        mPreviewView?.let { previewView ->
                            val coordinateLine = createCoordinateLine(
                                starX, starY,
                                ev.x, ev.y
                            )
                            previewView.setLine(coordinateLine)
                        }

                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateLine = createCoordinateLine(
                                starX, starY,
                                ev.x, ev.y
                            )
                            mPreviewView?.setLine(coordinateLine)
                            mTouchOptModel.updateICoordinate(coordinateLine)
                        } else {
                            mPreviewView?.clearLine()
                        }
                        isFirst = true
                    }
                }
            }
        }
        return true
    }


    protected fun createCoordinateLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): CoordinateLine {
        return if (x1 + y1 > x2 + y2) {
            CoordinateLine(
                CoordinatePoint(x2, y2),
                CoordinatePoint(x1, y1)
            )
        } else {
            CoordinateLine(
                CoordinatePoint(x1, y1),
                CoordinatePoint(x2, y2)
            )
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
}