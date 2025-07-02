package com.nwq.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.R


/**
create by: 86136
create time: 2023/5/22 14:51
Function description:
 */
class PreviewCoordinateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TouchOptView(context, attrs, defStyleAttr) {

    private val list = mutableListOf<PreviewCoordinateData>()
    private val TAG = "PreviewCoordinateView"
    private val mDotPaint: Paint  //用来画点的
    private val oblongPaint: Paint //用来画长方形的
    private val dotSize: Float

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

    public fun updateList(datas: List<PreviewCoordinateData>) {
        list.clear()
        list.addAll(datas)
        if (isVisible)
            invalidate()
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float, color: Int, size: Float) {
        mDotPaint.color = color
        canvas.drawCircle(x, y, dotSize, mDotPaint)
    }

    private fun drawArea(canvas: Canvas, area: CoordinateArea, color: Int, size: Float) {
        oblongPaint.color = color
        oblongPaint.strokeWidth = size
        Log.i(TAG, "drawArea: ${area.toString()}")
        if (area.isRound) {
            canvas.drawCircle(
                area.x.toFloat(), area.y.toFloat(),
                Math.min(
                    (area.x + area.width).toFloat(), (area.y + area.height).toFloat()
                ),
                oblongPaint,
            )
        } else {
            canvas.drawRect(
                area.x.toFloat(),
                area.y.toFloat(),
                (area.x + area.width).toFloat(),
                (area.y + area.height).toFloat(),
                oblongPaint
            )
        }
    }


    private fun drawLine(canvas: Canvas, line: CoordinateLine, color: Int, size: Float) {
        oblongPaint.color = color
        oblongPaint.strokeWidth = size
        canvas.drawLine(line.startP.x.toFloat(), line.startP.y.toFloat(), line.endP.x.toFloat(), line.endP.y.toFloat(), oblongPaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        if (!isVisible) {
//            Log.i(TAG, "onDraw: 不可见")
//            return
//        }
        list.forEach {
            when (it.coordinate) {
                is CoordinatePoint -> {
                    drawPoint(
                        canvas,
                        it.coordinate.x.toFloat(),
                        it.coordinate.y.toFloat(),
                        it.color,
                        it.paintWith
                    )
                }

                is CoordinateArea -> {
                    drawArea(
                        canvas,
                        it.coordinate,
                        it.color,
                        it.paintWith
                    )
                }

                is CoordinateLine -> {
                    drawLine(
                        canvas,
                        it.coordinate,
                        it.color,
                        it.paintWith
                    )
                }
            }
        }
        oblongLine?.let { line ->
            drawLine(canvas, line, ContextCompat.getColor(context, R.color.red), 5f)
        }

        oblongArea?.let {
            drawArea(canvas, it, ContextCompat.getColor(context, R.color.red), 5f)
        }
    }

    // 预览相关的方法
    override fun updatePreviewArea(area: CoordinateArea) {
        Log.i(TAG, "updatePreviewArea: ${area.toString()}")
        oblongArea = area
        invalidate()
    }

    protected override fun clearPreviewArea() {
        oblongArea = null
        invalidate()
    }

    protected override fun updatePreviewLine(line: CoordinateLine) {
        Log.i(TAG, "updatePreviewLine: ${line.toString()}")
        oblongLine = line
        invalidate()
    }

    override fun clearPreviewLine() {
        oblongLine = null
        invalidate()
    }

}