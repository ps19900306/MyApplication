package com.nwq.checkhsv

import kotlin.math.abs

//白色的特别用 eve菜单按钮的颜色
class CheckHSVSame3 : CheckHSVSame {


   //差不多有50的饱和度差距 如果背景是全红的情况下
   override fun checkHSVSame(h1:Double, s1:Double, v1:Double, h2:Double, s2:Double, v2:Double): Boolean{
       if ( s1 < 60 && s2 < 60 && v1 > 110 && v2 > 110) {
           return (abs(h1 - h2) <= 180) &&
                   (abs(s1 - s2) <= 60) &&
                   (abs(v1 - v2) <= 60)
       }
       return abs(h1 - h2) <= 1 &&
               abs(s1 - s2) <= 1 &&
               abs(v1 - v2) <= 1
   }


}