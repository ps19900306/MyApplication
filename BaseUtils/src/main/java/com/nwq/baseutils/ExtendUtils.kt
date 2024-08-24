package com.nwq.baseutils

import android.graphics.Bitmap
import android.view.View
import android.widget.Checkable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


fun Int.toRgbInt(): Int {
    return if (this > 255) {
        255
    } else if (this <= 0) {
        0
    } else {
        this
    }
}

inline fun <T : View> T.singleClick(time: Long = 800, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

//兼容点击事件设置为this的情况
fun <T : View> T.singleClick(onClickListener: View.OnClickListener, time: Long = 800) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            onClickListener.onClick(this)
        }
    }
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0


fun Bitmap.isLandscape(): Boolean {
    return width > height
}

fun Bitmap.isVertical(): Boolean {
    return height > width
}

fun CoroutineScope.runOnUI(doInUI: suspend CoroutineScope.() -> Unit): Job {
    return launch(Dispatchers.Main.immediate) { doInUI() }
}

fun CoroutineScope.runOnIO(doInIO: suspend CoroutineScope.() -> Unit): Job {
    return launch(Dispatchers.IO) { doInIO() }
}

