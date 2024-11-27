package com.example.myapplication.opencv

import androidx.lifecycle.ViewModel
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat

class AutoCodeModel : ViewModel() {

    private var selectArea: CoordinateArea? = null
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


    fun addHSVRule(rule: HSVRule) {
        hSVRuleList.add(rule)
    }
}