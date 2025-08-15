package com.nwq.simplelist

import com.nwq.baseutils.ContextUtils

class TextResWarp(private val resId: Int) : IText<Int> {
    override fun getText(): String {
        return ContextUtils.getContext().getString(resId)
    }

    override fun getT(): Int {
        return resId
    }
}