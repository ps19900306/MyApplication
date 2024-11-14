package com.nwq.exculde

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake
import kotlinx.coroutines.delay
import org.opencv.core.Mat

class BaseController(
    val acService: AccessibilityService,
) {


    private val imgTake = ImgTake.imgTake
    private val takeScreenIn = 4000L

    private var matMap = HashMap<CoordinateArea, Mat>()


}