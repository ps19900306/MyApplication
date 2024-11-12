package com.nwq.imgtake

import android.graphics.Bitmap

interface ImgTake {

    companion object {
        //这个必须初始化ImgTake的一个子类 并赋值
        lateinit var imgTake: ImgTake
    }

    suspend fun takeScreenImg(): Bitmap?
}