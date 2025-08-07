package com.nwq.baseutils

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

//进行文字的简单拷贝
object ClipboardUtils {

    public fun copyText(text: String, context: Context = ContextUtils.getContext()) {
        val clipboardManager = ContextUtils.getContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    public fun getText(context: Context = ContextUtils.getContext()): String? {
        val clipboardManager = ContextUtils.getContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        return clipData?.getItemAt(0)?.text?.toString()
    }



}