package com.nwq.opencv.auto_point_impl

import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

abstract class AutoPointImpl() : IAutoRulePoint {

    abstract fun getHSVRuleList(): List<HSVRule>
    private val tagStr: String = ""
    override suspend fun autoPoint(hsvMat: Mat): List<Point> {
        val pointList = mutableListOf<Point>()
        getHSVRuleList().forEach {
            val list =
                MatUtils.getCornerPoint(
                    hsvMat,
                    it.minH,
                    it.maxH,
                    it.minS,
                    it.maxS,
                    it.minV,
                    it.maxV
                )
            pointList.addAll(list)
        }

        return pointList
    }

    override fun getTag(): String {
        return tagStr
    }


    // 获取高亮区域
    private fun getHighSvRule() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 195, 255, 220, 255)
            list.add(rule)
        }
    }

    //获取浅色
    private fun getHighSvRule2() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 135, 195, 220, 255)
            list.add(rule)
        }
    }


    //获取颜色偏黑色
    private fun getHighSvRule3() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 135, 255, 160, 220)
            list.add(rule)
        }
    }


    data class Point(val x: Int, val y: Int)
    data class Area(val x: Int, val y: Int, val width: Int, val height: Int)

    fun isPointWithinDistance(point: Point, area: Area, distance: Int): Boolean {
        // 计算矩形边界
        val left = area.x
        val right = area.x + area.width
        val top = area.y
        val bottom = area.y + area.height

        // 判断点是否在扩展后的矩形区域内
        val extendedLeft = left - distance
        val extendedRight = right + distance
        val extendedTop = top - distance
        val extendedBottom = bottom + distance

        return point.x in extendedLeft..extendedRight && point.y in extendedTop..extendedBottom
    }

    fun filterPoints(points: List<Point>, area: Area, distance: Int): List<Point> {
        return points.filter { point -> !isPointWithinDistance(point, area, distance) }
    }

    fun main() {
        val points = listOf(
            Point(1, 1),
            Point(5, 5),
            Point(10, 10),
            Point(15, 15)
        )
        val area = Area(5, 5, 10, 10)
        val distance = 3

        val filteredPoints = filterPoints(points, area, distance)
        println(filteredPoints)
    }
}