package com.nwq.exculde

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake
import kotlinx.coroutines.delay
import org.opencv.core.Mat

abstract class BaseController(
    val acService: AccessibilityService,
) {



    abstract val TAG:String
    private val imgTake = ImgTake.imgTake



}