package com.nwq.checkhsv

class CheckHSVSame1 : CheckHSVSame {

   override fun checkHSVSame(h1:Double, s1:Double, v1:Double, h2:Double, s2:Double, v2:Double): Boolean{
       return h1 == h2 && s1 == s2 && v1 == v2
   }


}