package com.nwq.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.R


/**
create by: 86136
create time: 2023/5/22 14:51
Function description:
 */
class PreviewImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TouchOptView(context, attrs, defStyleAttr) {

    private val TAG = "PreviewImageView"

    //是否显示预览
    private var showFlag = true
    private val mDotPaint: Paint  //用来画点的
    private val oblongPaint: Paint //用来画长方形的
    private val dotSize: Float

    val dotList = mutableListOf<CoordinatePoint>()
    var areaList = mutableListOf<CoordinateArea>()
    var lineList = mutableListOf<CoordinateLine>()

    //这个是用于查看选中的
    private val watchDotList = mutableListOf<CoordinatePoint>()

    //单区域预览
    var oblongArea: CoordinateArea? = null

    //单区域预览
    var oblongLine: CoordinateLine? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreviewImageView)
        dotSize = typedArray.getDimension(R.styleable.PreviewImageView_dotSize, 1F)
        val dotColor = typedArray.getColor(
            R.styleable.PreviewImageView_dotColor, ContextCompat.getColor(context, R.color.red)
        )
        val oblongSize = typedArray.getDimension(R.styleable.PreviewImageView_oblongSize, 1F)
        val oblongColor = typedArray.getColor(
            R.styleable.PreviewImageView_oblongColor, ContextCompat.getColor(context, R.color.green)
        )
        mDotPaint = Paint()
        mDotPaint.color = dotColor


        oblongPaint = Paint()
        oblongPaint.color = oblongColor
        oblongPaint.strokeWidth = oblongSize
        oblongPaint.setStyle(Paint.Style.STROKE)
    }

    fun setArea(area: CoordinateArea?) {
        oblongArea = area
        invalidate()
    }

    fun setLine(line: CoordinateLine?) {
        oblongLine = line
        invalidate()
    }

    fun addArea(coordinate: CoordinateArea): Boolean {
        if (!areaList.contains(coordinate)) {
            areaList.add(coordinate)
            invalidate()
            return true
        }
        return false
    }

    fun addLine(line: CoordinateLine): Boolean {
        if (!lineList.contains(line)) {
            lineList.add(line)
            invalidate()
            return true
        }
        return false
    }

    fun clearLine() {
        oblongLine = null
        lineList.clear()
    }

    fun removeArea(coordinate: CoordinateArea) {
        if (areaList.contains(coordinate)) {
            areaList.remove(coordinate)
            invalidate()
        }
    }

    fun clearArea() {
        areaList.clear()
    }


    fun addDot(coordinate: CoordinatePoint): Boolean {
        if (!dotList.contains(coordinate)) {
            dotList.add(coordinate)
            invalidate()
            return true
        }
        return false
    }

    fun removeDot(coordinate: CoordinatePoint) {
        if (dotList.contains(coordinate)) {
            dotList.remove(coordinate)
            invalidate()
        }
    }

    fun clearPoint() {
        dotList.clear()
    }

    fun setPointList(list: List<CoordinatePoint>) {
        dotList.clear()
        dotList.addAll(list)
        invalidate()
    }

    fun setWatchDotList(list: List<CoordinatePoint>) {
        watchDotList.clear()
        watchDotList.addAll(list)
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!showFlag) {
            return
        }
        dotList.forEach {
            canvas.drawCircle(it.xF, it.yF, dotSize, mDotPaint)
        }
        watchDotList.forEach {
            canvas.drawCircle(it.xF, it.yF, dotSize, mDotPaint)
        }

        areaList.forEach {
            if (it.isRound) {
                canvas.drawCircle(
                    it.xF, it.yF,
                    Math.min(
                        (it.x + it.width).toFloat(), (it.y + it.height).toFloat()
                    ),
                    oblongPaint,
                )
            } else {
                canvas.drawRect(
                    it.x.toFloat(),
                    it.y.toFloat(),
                    (it.x + it.width).toFloat(),
                    (it.y + it.height).toFloat(),
                    oblongPaint
                )
            }
        }
        lineList.forEach { line ->
            canvas.drawLine(line.startP.xF, line.startP.yF, line.endP.xF, line.endP.yF, oblongPaint)
        }

        oblongLine?.let { line ->
            canvas.drawLine(line.startP.xF, line.startP.yF, line.endP.xF, line.endP.yF, oblongPaint)
        }

        oblongArea?.let {
            if (it.isRound) {
                canvas.drawCircle(
                    it.x.toFloat() + it.width / 2,
                    it.y.toFloat() + it.height / 2,
                    (it.width.coerceAtMost(it.height) / 2).toFloat(),
                    oblongPaint
                )
            } else {
                canvas.drawRect(
                    it.x.toFloat(),
                    it.y.toFloat(),
                    (it.x + it.width).toFloat(),
                    (it.y + it.height).toFloat(),
                    oblongPaint
                )
            }

        }
    }

    // 预览相关的方法
    override protected fun updatePreviewArea(area: CoordinateArea) {
        Log.i(TAG, "updatePreviewArea: ${area.toString()}")
        oblongArea = area
        invalidate()
    }
    override protected fun clearPreviewArea() {
        oblongArea = null
        invalidate()
    }
    override protected fun updatePreviewLine(line: CoordinateLine) {
        Log.i(TAG, "updatePreviewLine: ${line.toString()}")
        oblongLine = line
        invalidate()
    }
    override protected fun clearPreviewLine() {
        oblongLine = null
        invalidate()
    }

}