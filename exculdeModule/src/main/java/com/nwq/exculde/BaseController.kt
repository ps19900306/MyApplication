package com.nwq.exculde

import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.nwq.imgtake.ImgTake

class BaseController(
    val acService: AccessibilityService,
) {





    private val imgTake: ImgTake by lazy {
        ImgTake.imgTake
    }




}