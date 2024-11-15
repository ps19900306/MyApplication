package com.android.system.taker

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TimeAccessibilityService : AccessibilityService() {

    private val TAG = this::class.java.simpleName






    companion object {
        const val Intent_Filter_TAG = "schedule.cmd.v3"
        const val CMD = "cmd"
    }

    private val communicationBroadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { dealEvent(it) }
            }
        }
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(communicationBroadcast, IntentFilter.create(Intent_Filter_TAG, "cmd/int"))
    }

    override fun onInterrupt() {
        unregisterReceiver(communicationBroadcast)
    }

    fun dealEvent(intent: Intent) {
    }


}