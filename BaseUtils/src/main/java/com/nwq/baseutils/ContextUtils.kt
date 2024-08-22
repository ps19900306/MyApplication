package com.nwq.baseutils

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextUtils {

    private lateinit var context: Context

    fun getContext(): Context {
        return context
    }

    fun init(context: Context) {
        this.context = context
    }


}