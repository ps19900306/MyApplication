package com.nwq.optlib.bean

import com.nwq.BeCode


class GrayRule(
    var min: Int = 0,
    var max: Int = 180,
) : BeCode {

    fun verificationRule(p: Int): Boolean {
        return p in min..max
    }

    override fun codeString(): String {
        return "GrayRule($min, $max)"
    }

}