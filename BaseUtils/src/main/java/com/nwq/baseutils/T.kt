package com.nwq.baseutils

import android.os.Handler
import android.os.Looper
import android.widget.Toast

object T {

    val context
        get() = ContextUtils.getContext()
    val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun show(message: CharSequence) {
        mHandler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun show(resId: Int) {
        mHandler.post {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        }
    }
}