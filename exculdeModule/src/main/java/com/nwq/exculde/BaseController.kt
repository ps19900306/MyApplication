package com.nwq.exculde

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import com.nwq.imgtake.ImgTake
import kotlinx.coroutines.delay

class BaseController(
    val acService: AccessibilityService,
) {


    private val imgTake = ImgTake.imgTake
    private val takeScreenIn = 4000L



}