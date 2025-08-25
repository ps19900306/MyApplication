package com.nwq.optlib.bean


import com.nwq.BeCode
import com.nwq.baseobj.CoordinatePoint
import org.opencv.core.Mat


//单点对应单色
class PointHSVRule(val point: CoordinatePoint, var rule: HSVRule) : BeCode {


    constructor(x: Int, y: Int, h: Int, s: Int, v: Int) : this(
        CoordinatePoint(x, y),
        HSVRule.getSimple(h, s, v)
    )

    fun checkBIpr(srcMat: Mat, offsetX: Int, offsetY: Int): Boolean {
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

    override fun codeString(): String {
        return "PointHSVRule(${point.codeString()},${rule.codeString()})"
    }


}