package com.nwq.baseutils

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint

object CoordinateUtils{

    fun calculateBoundingRectangle(points: List<CoordinatePoint>): CoordinateArea {
        // 初始化边界值
        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE

        // 遍历所有点，更新边界值
        for (point in points) {
            if (point.x < minX) minX = point.x
            if (point.x > maxX) maxX = point.x
            if (point.y < minY) minY = point.y
            if (point.y > maxY) maxY = point.y
        }

        // 根据边界值创建矩形的四个顶点
        return CoordinateArea(
            minX,
            minY,
            maxX - minX,
            maxY - minY,
        )
    }


//    fun calculateBoundingRectangle(coordinateArea: CoordinateArea): CoordinateArea {
//        // 初始化边界值
//        var minX = Int.MAX_VALUE
//        var maxX = Int.MIN_VALUE
//        var minY = Int.MAX_VALUE
//        var maxY = Int.MIN_VALUE
//
//        // 遍历所有点，更新边界值
//        for (point in points) {
//            if (point.x < minX) minX = point.x
//            if (point.x > maxX) maxX = point.x
//            if (point.y < minY) minY = point.y
//            if (point.y > maxY) maxY = point.y
//        }
//
//        // 根据边界值创建矩形的四个顶点
//        return CoordinateArea(
//            minX,
//            minY,
//            maxX - minX,
//            maxY - minY,
//        )
//    }


}

