package com.nwq.optlib.bean

import com.nwq.BeCode


class HSVRule(
    var minH: Int = 0,
    var maxH: Int = 180,
    var minS: Int = 0,
    var maxS: Int = 255,
    var minV: Int = 0,
    var maxV: Int = 255
) : BeCode {

    companion object {

        fun getSimple(
            h: Int,
            s: Int,
            v: Int,
        ): HSVRule {
            return HSVRule(h, h, s, s, v, v)
        }

    }

    fun verificationRule(h: Int, s: Int, v: Int): Boolean {
        return h in minH..maxH && s in minS..maxS && v in minV..maxV
    }

    // 这个字段将不会被序列化和反序列化
    @Transient
    private var mSelected = false

    fun getIsSelected(): Boolean {
        return mSelected
    }

    fun setIsSelected(isSelected: Boolean) {
        mSelected = isSelected
    }

    override fun codeString(): String {
        return "HSVRule($minH, $maxH, $minS, $maxS, $minV, $maxV)"
    }

    fun toStringSimple(): String {
        return "HSVRule(nH=$minH, xH=$maxH, nS=$minS, xS=$maxS, nV=$minV, xV=$maxV)"
    }
}