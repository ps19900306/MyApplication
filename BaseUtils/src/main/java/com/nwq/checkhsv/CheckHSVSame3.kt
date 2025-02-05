package com.nwq.checkhsv

import kotlin.math.abs

//白色的特别用
class CheckHSVSame3 : CheckHSVSame {

   override fun checkHSVSame(h1:Double, s1:Double, v1:Double, h2:Double, s2:Double, v2:Double): Boolean{
       if ( s1 < 30 && s2 < 30 && v1 > 140 && v2 > 140) {
           return (abs(h1 - h2) <= 180) &&
                   (abs(s1 - s2) <= 180) &&
                   (abs(v1 - v2) <= 180)
       }
       return abs(h1 - h2) <= 1 &&
               abs(s1 - s2) <= 1 &&
               abs(v1 - v2) <= 1
   }


}