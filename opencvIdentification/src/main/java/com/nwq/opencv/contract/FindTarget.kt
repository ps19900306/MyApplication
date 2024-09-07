package com.nwq.opencv.contract

import com.nwq.baseobj.CoordinateArea


interface FindTarget{

   fun findTarget(any: Any): CoordinateArea?

   fun release()
}