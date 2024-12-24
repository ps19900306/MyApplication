package com.nwq.opencv.data

data class PointVerifyResult(
    var x: Int,
    var y: Int,
    var h: Int,
    var s: Int,
    var v: Int,
    var isPass: Boolean
)