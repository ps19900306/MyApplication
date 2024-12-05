package com.nwq.adapter

import com.nwq.baseutils.ContextUtils

data class ResStrKeyText(val resId: Int) : IKeyText {
    override fun getText(): String {
        return ContextUtils.getContext().getString(resId)
    }

    override fun getKey(): Int {
        return resId
    }
}
