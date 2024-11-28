package com.nwq.opencv.click

import android.accessibilityservice.GestureDescription
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.opencv.exhaustion.ClickParameter
import com.nwq.opencv.exhaustion.ExhaustionControl
import com.nwq.opencv.exhaustion.OptRange
import com.nwq.opencv.exhaustion.OptSlide
import android.graphics.Path

object ClickBuilderUtils {

    fun buildClick(
        coordinateArea: CoordinateArea,
        clickArea: ClickArea,
        delayTime: Long = 0L
    ): GestureDescription? {
        val parameter = ExhaustionControl.getClickParameter()
        val point1 = if (!clickArea.isRound) {
            builderSquare(
                parameter,
                CoordinateArea(
                    coordinateArea.x + clickArea.offsetX,
                    coordinateArea.y + clickArea.offsetY,
                    clickArea.width,
                    clickArea.height,
                )
            )
        } else {
            builderRotundity(
                parameter,
                CoordinateArea(
                    coordinateArea.x + clickArea.offsetX,
                    coordinateArea.y + clickArea.offsetY,
                    clickArea.width,
                    clickArea.height,
                )
            )
        }
        val list = mutableListOf<CoordinatePoint>()
        list.add(point1)
        addSlidePoint(parameter, list, coordinateArea)
        val builder = GestureDescription.Builder()
        val path = Path()
        list.forEachIndexed { index, coordinatePoint ->
            if (index == 0) {
                path.moveTo(coordinatePoint.x.toFloat(), coordinatePoint.y.toFloat())
            }
        }
        builder.addStroke(
            GestureDescription.StrokeDescription(
                path, delayTime, ExhaustionControl.getClickDuration(parameter.optDuration)
            )
        )
        return builder.build()
    }


    private fun builderSquare(
        parameter: ClickParameter, coordinateArea: CoordinateArea
    ): CoordinatePoint {
        return getRandomRangePoint(
            parameter.optRange,
            coordinateArea.x,
            coordinateArea.y,
            coordinateArea.width,
            coordinateArea.height
        )
    }


    private fun addSlidePoint(
        parameter: ClickParameter,
        list: MutableList<CoordinatePoint>,
        coordinateArea: CoordinateArea
    ) {
        val point1 = list[0]
        when (parameter.optSlide) {
            OptSlide.NOT_SLIDE -> {

            }

            OptSlide.SLIDE_ONE -> {
                list.add(
                    CoordinatePoint(
                        (point1.x + (Math.random() - 0.5) * 0.05 * coordinateArea.width).toInt(),
                        (point1.y + (Math.random() - 0.5) * 0.05 * coordinateArea.height).toInt()
                    )
                )
            }

            OptSlide.SLIDE_TWO -> {
                list.add(
                    CoordinatePoint(
                        (point1.x + (Math.random() - 0.5) * 0.05 * coordinateArea.width).toInt(),
                        (point1.y + (Math.random() - 0.5) * 0.05 * coordinateArea.height).toInt()
                    )
                )
                list.add(
                    CoordinatePoint(
                        (point1.x + (Math.random() - 0.5) * 0.05 * coordinateArea.width).toInt(),
                        (point1.y + (Math.random() - 0.5) * 0.05 * coordinateArea.height).toInt()
                    )
                )
            }
        }
    }

   private fun getRandomRangePoint(
        optRange: Int,
        x: Int,
        y: Int,
        with: Int,
        height: Int
    ): CoordinatePoint {
        val range = when (optRange) {
            OptRange.SMALL_PRECISION -> {
                Math.random() * 0.4 + 0.3
            }

            OptRange.WIDE_RANGE -> {
                Math.random() * 0.6 + 0.2
            }

            OptRange.FULL_RANGE -> {
                Math.random() * 0.8 + 0.1
            }

            OptRange.ALL_OPT_RANGE -> {
                Math.random() * 1.0
            }

            else -> {
                Math.random() * 1.0
            }
        }
        return CoordinatePoint((x + range * with).toInt(), (y + range * height).toInt());
    }


    private fun builderRotundity(
        parameter: ClickParameter, coordinateArea: CoordinateArea
    ): CoordinatePoint {
        val du = Math.random() * 2 * Math.PI
        val minimumDiameter = Math.min(coordinateArea.height, coordinateArea.width) / 2 //最小半径
        val centerPoint = CoordinatePoint(
            coordinateArea.x + coordinateArea.width / 2,
            coordinateArea.y + coordinateArea.height / 2
        ) //中心点
        return when (parameter.optRange) {
            OptRange.SMALL_PRECISION -> {
                val length = Math.random() * 0.4 * minimumDiameter
                CoordinatePoint(
                    centerPoint.x + Math.cos(du) * length,
                    centerPoint.y + +Math.sin(du) * length
                )
            }

            OptRange.WIDE_RANGE -> {
                val length = Math.random() * 0.6 * minimumDiameter
                CoordinatePoint(
                    centerPoint.x + Math.cos(du) * length,
                    centerPoint.y + +Math.sin(du) * length
                )
            }

            OptRange.FULL_RANGE -> {
                val length = Math.random() * 0.8 * minimumDiameter
                CoordinatePoint(
                    centerPoint.x + Math.cos(du) * length,
                    centerPoint.y + +Math.sin(du) * length
                )
            }

            OptRange.ALL_OPT_RANGE -> {
                val length = Math.random() * minimumDiameter
                CoordinatePoint(
                    centerPoint.x + Math.cos(du) * length,
                    centerPoint.y + +Math.sin(du) * length
                )
            }

            else -> {
                val length = Math.random() * 0.8 * minimumDiameter
                CoordinatePoint(
                    centerPoint.x + Math.cos(du) * length,
                    centerPoint.y + +Math.sin(du) * length
                )
            }
        }
    }


}