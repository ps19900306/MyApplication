package com.example.myapplication.data

import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule

data class VerifyResultPointSummarize(
    val pointRule: PointRule?=null,
    val pointHSVRule: PointHSVRule?=null,
    var poinitInfo: MutableList<PointVerifyResult> = mutableListOf(),
    var passCount: Int = 0,
    var failCount: Int = 0,
)
