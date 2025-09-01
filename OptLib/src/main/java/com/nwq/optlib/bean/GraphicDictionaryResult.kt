package com.nwq.optlib.bean

import com.nwq.baseobj.CoordinateArea
import org.opencv.core.Mat

data class GraphicDictionaryResult(
    var resultStr: String,
    var coordinateArea: CoordinateArea,
    var mat: Mat?=null
)