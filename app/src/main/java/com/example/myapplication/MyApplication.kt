package com.example.myapplication


import android.app.Application
import org.opencv.android.OpenCVLoader

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        OpenCVLoader.initLocal()
    }
}
