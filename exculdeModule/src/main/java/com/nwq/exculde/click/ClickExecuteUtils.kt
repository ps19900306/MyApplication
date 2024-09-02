package com.nwq.exculde.click

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first


class ClickExecuteUtils {

    //准备
    private val PREPARING = 1

    // 执行中
    private val EXECUTING = 2

    // 执行失败
    private val EXECUTION_FAILED = 4

    // 执行成功
    private val EXECUTION_SUCCESS = 8

    private val runStateFlow: MutableStateFlow<Int> = MutableStateFlow(PREPARING)
    private var cancelDescription: GestureDescription.StrokeDescription? = null
    lateinit var aService: AccessibilityService

    suspend fun publishClickTask(
        gesture: GestureDescription,
        cancelDescription: GestureDescription.StrokeDescription,
        interrupt: Boolean = false,
    ) {
        if (runStateFlow.value >= EXECUTION_FAILED) {
            executeClick(aService, gesture)
            this.cancelDescription = cancelDescription
        } else {
            if (interrupt) {
                runStateFlow.value = PREPARING
                executeClick(aService, gesture)
                this.cancelDescription = cancelDescription
            } else {
                runStateFlow
                    .first { it >= EXECUTION_FAILED }
                    .let {
                        executeClick(aService, gesture)
                        this.cancelDescription = cancelDescription
                    }
            }
        }
    }


    suspend fun executeClick(
        aService: AccessibilityService,
        gesture: GestureDescription
    ) {
        runStateFlow.value = EXECUTING
        aService.dispatchGesture(
            gesture, object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                    overPress()
                    runStateFlow.value = EXECUTION_FAILED
                }

                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    overPress()
                    runStateFlow.value = EXECUTION_SUCCESS
                }
            }, null
        )
    }


    private fun overPress() {
        cancelDescription?.let {
            aService.dispatchGesture(
                GestureDescription.Builder().addStroke(it).build(), null, null
            )
        }
        cancelDescription = null
    }

}