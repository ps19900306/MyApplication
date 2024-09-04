package com.nwq.opencv.contract

import com.nwq.baseobj.Area


interface FindTarget{

   fun findTarget(any: Any): Area?

   fun release()
}