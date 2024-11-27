package com.example.myapplication.opencv

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

class AutoCodeModel : ViewModel() {

    private var selectArea: CoordinateArea? = null
    private var srcBitmap: Bitmap? = null
    private var selectMat: Mat? = null   //hsvMat图
    private var clickArea: CoordinateArea? = null
    private var hSVRuleList: MutableList<HSVRule> = mutableListOf()

    private fun clearHSVRuleList() {
        hSVRuleList.clear()
    }


    fun addHSVRule(x: Int, y: Int) {
        val d = selectMat?.get(y, x) ?: return
        addHSVRule(d[0], d[1], d[2])
    }

    fun addHSVRule(h: Double, s: Double, v: Double) {

    }


    fun performAutomaticEncoding() {
        val mat = selectMat ?: return
        val bitmap = srcBitmap ?: return
        var area = selectArea ?: return
        val pointList = mutableListOf<Point>()
        hSVRuleList.forEach {
            val list =
                MatUtils.getCornerPoint(mat, it.minH, it.maxH, it.minS, it.maxS, it.minV, it.maxV)
            pointList.addAll(list)
        }

        buildRgbFindTarget(pointList)
        buildHsvFindTarget(pointList)

    }

    private fun buildHsvFindTarget(pointList: MutableList<Point>) {

    }

    private fun buildRgbFindTarget(pointList: MutableList<Point>) {

    }


    fun addHSVRule(rule: HSVRule) {
        hSVRuleList.add(rule)
    }


    // 获取高亮区域
    private fun getHighSvRule() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 180, 255, 220, 255)
            list.add(rule)
        }
    }

    // 获取高亮区域
    private fun getHighSvRule2() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 100, 180, 220, 255)
            list.add(rule)
        }
    }


}