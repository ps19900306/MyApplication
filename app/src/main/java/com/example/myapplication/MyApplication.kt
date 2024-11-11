package com.example.myapplication


import android.app.Application
import com.nwq.baseutils.ContextUtils
import org.opencv.android.OpenCVLoader

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        OpenCVLoader.initLocal()
        ContextUtils.init(this);
    }
}
