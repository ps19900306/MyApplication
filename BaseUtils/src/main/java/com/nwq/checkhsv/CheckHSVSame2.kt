package com.nwq.checkhsv


import kotlin.math.abs

class CheckHSVSame2 : CheckHSVSame {



    override fun checkHSVSame(h1: Double, s1: Double, v1: Double, h2: Double, s2: Double, v2: Double): Boolean {
        //高黑度的时候允许的误差变大
        if (v1 < 10 && v2 < 10) {
            return abs(h1 - h2) <= 10 &&
                    abs(s1 - s2) <= 10 &&
                    abs(v1 - v2) <= 3
        }

        //白色的时候允许的误差变大
        if (v1 > 230 && v2 > 230 && s1 < 20 && s2 < 20) {
            return abs(h1 - h2) <= 10 &&
                    abs(s1 - s2) <= 5 &&
                    abs(v1 - v2) <= 5
        }

        return abs(h1 - h2) <= 1 &&
                abs(s1 - s2) <= 1 &&
                abs(v1 - v2) <= 1

    }


}