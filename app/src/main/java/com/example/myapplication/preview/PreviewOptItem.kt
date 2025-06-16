package com.example.myapplication.preview

import com.example.myapplication.base.TouchOptModel
import com.nwq.baseobj.ICoordinate

/**
 * TouchOptModel.RECT_AREA_TYPE
 */
data class PreviewOptItem(
    val key: Int,
    val type: Int,
    val resStr: Int = key,
    val color: Int = -1,
    val paintWith: Float = 1f,
    var coordinate: ICoordinate? = null
)
