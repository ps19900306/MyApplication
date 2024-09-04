package com.nwq.opencv.rgb.point_rule


import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.nwq.opencv.rgb.RGBInfo
import com.nwq.opencv.rgb.area.CoordinateArea
import com.nwq.opencv.rgb.area.CoordinatePoint
import com.nwq.opencv.rgb.color_rule.ColorCompareRule
import com.nwq.opencv.rgb.color_rule.ColorRule

//单点对应单色
class TwoAreaRule(
    private val area1: CoordinateArea,
    private val area2: CoordinateArea,
    val rule: ColorCompareRule,
    private val removeAcnode: Boolean = true//去除孤点 比如对比亮度差距的时候，去除掉背景透明度下特别的点
) : IPR {

    override fun getCoordinatePoint(): CoordinatePoint {
        return CoordinatePoint(area1.x, area1.y)
    }

    override fun getColorRule(): ColorRule? {
        return rule
    }

    override fun checkIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        val pixels1 = IntArray(area1.width * area1.height)
        bitmap.getPixels(
            pixels1,
            0,
            area1.width,
            area1.x + offsetX,
            area1.y + offsetY,
            area1.width,
            area1.height
        )
        val pixels2 = IntArray(area2.width * area2.height)
        bitmap.getPixels(
            pixels2,
            0,
            area2.width,
            area2.x + offsetX,
            area2.y + offsetY,
            area2.width,
            area2.height
        )

        val rgbInfo1 = obtainAverageColor(pixels1)
        val rgbInfo2 = obtainAverageColor(pixels2)

        return rule.optRgbInfo(rgbInfo1, rgbInfo2)

    }


    private fun obtainAverageColor(array: IntArray): RGBInfo {
        return if (removeAcnode) {
     //       groupPoint(array)
            var rTotal = 0
            var gTotal = 0
            var bTotal = 0
            array.forEach {
                rTotal += it.red
                gTotal += it.green
                bTotal += it.blue
            }
            RGBInfo(rTotal / array.size, gTotal / array.size, bTotal / array.size)
        } else {
            var rTotal = 0
            var gTotal = 0
            var bTotal = 0
            array.forEach {
                rTotal += it.red
                gTotal += it.green
                bTotal += it.blue
            }
            RGBInfo(rTotal / array.size, gTotal / array.size, bTotal / array.size)
        }
    }


//    /**
//     * 自动对点进行分组 找到三个特征最高的关键
//     */
//    private fun groupPoint(array: IntArray): RGBInfo {
//        val colorMaps = mutableMapOf<FeaturePointKey, MutableList<FeatureCoordinatePoint>>()
//        val featureKeyList = mutableListOf<FeaturePointKey>()
//        array.forEach { colorInt ->
//            val point = FeatureCoordinatePoint(colorInt)
//            val key = featureKeyList.find { it.isInRange(point) }
//            if (key != null) {
//                key.pointCount++
//                point.mFeaturePointKey = key
//                colorMaps[key]?.add(point)
//            } else {
//                val newKey = FeaturePointKey(colorInt)
//                newKey.pointCount++
//                point.mFeaturePointKey = newKey
//                val list = mutableListOf<FeatureCoordinatePoint>()
//                list.add(point)
//                featureKeyList.add(newKey)
//                colorMaps[newKey] = list
//            }
//        }
//
//        var count = 1
//        var mFeaturePointKey: FeaturePointKey? = null
//        featureKeyList.forEach {
//            if (it.pointCount > count) {
//                count = it.pointCount
//                mFeaturePointKey = it
//            }
//        }
//
//        var rTotal = 0
//        var gTotal = 0
//        var bTotal = 0
//        colorMaps[mFeaturePointKey]?.forEach {
//            rTotal += it.red
//            gTotal += it.green
//            bTotal += it.blue
//        }
//
//        return RGBInfo(rTotal / count, gTotal / count, bTotal / count)
//    }


}