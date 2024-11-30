package com.nwq.opencv.hsv


import com.nwq.baseobj.CoordinatePoint
import com.nwq.opencv.point_rule.MIPR
import com.nwq.opencv.rgb.ColorRule
import org.opencv.core.Mat


//单点对应单色
class PointHSVRule(val point: CoordinatePoint, val rule: HSVRule) : MIPR() {


    constructor(x: Int, y: Int, h: Int, s: Int, v: Int) : this(
        CoordinatePoint(x, y),
        HSVRule.getSimple(h, s, v)
    )

    override fun checkBIpr(srcMat: Mat, offsetX: Int, offsetY: Int): Boolean {
        // 检查点是否超出矩阵边界
        if (point.x + offsetX < 0 || point.x + offsetX >= srcMat.cols() || point.y + offsetY < 0 || point.y + offsetY >= srcMat.rows()) {
            return false
        }

        // 获取指定位置的像素值
        val array = srcMat.get(point.y + offsetY, point.x + offsetX)
        if (array == null || array.size != 3) {
            return false
        }

        // 验证像素值
        return rule.verificationRule(array[0].toInt(), array[1].toInt(), array[2].toInt())
    }

    override fun getCoordinatePoint(): CoordinatePoint {
        return point
    }


}