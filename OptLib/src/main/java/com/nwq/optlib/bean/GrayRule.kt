package com.nwq.optlib.bean


 class GrayRule(
    var min: Int = 0,
    var max: Int = 180,
) {

    fun verificationRule(p: Int): Boolean {
        return p in min..max
    }

}