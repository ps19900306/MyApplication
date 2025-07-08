package com.nwq.opencv

import android.graphics.MaskFilter
import androidx.annotation.IntDef

@IntDef(
    AutoHsvRuleType.FILTER_MASK,
    AutoHsvRuleType.RE_FILTER_MASK,
    AutoHsvRuleType.KEY_POINT,
    AutoHsvRuleType.RE_KEY_POINT,
    AutoHsvRuleType.BE_FRAME_POINT
    )
annotation class AutoHsvRuleType() {
    companion object {
        const val FILTER_MASK = 1  //条件过滤产出的MASK
        const val RE_FILTER_MASK = 2 //去掉对应颜色过滤产出Mask
        const val KEY_POINT = 3  //寻找关键点
        const val RE_KEY_POINT = 4//过滤掉对应颜色获取关键点
        const val BE_FRAME_POINT = 5//通过颜色块 然后获取边框获取关键点
    }
}