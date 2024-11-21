package com.nwq.exculde.click

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference


object ClickExecuteUtils {

    //准备
    private const val PREPARING = 1
    // 执行中
    private const  val EXECUTING = 2
    // 执行失败
    private const val EXECUTION_FAILED = 4
    // 执行成功
    private const val EXECUTION_SUCCESS = 8

    private val runStateFlow: MutableStateFlow<Int> = MutableStateFlow(PREPARING)
    private var cancelDescription: GestureDescription.StrokeDescription? = null
    //软引用AccessibilityService
    private var aService: WeakReference<AccessibilityService>? = null

    //Need int()
    fun setActivityService(service: AccessibilityService) {
        aService = WeakReference(service)
    }

    /**
     * 暂停函数，用于发布点击任务
     *
     * 此函数根据当前的执行状态来决定是否执行点击任务以及如何处理中断逻辑
     * 它通过[runStateFlow]的状态来判断何时应该发布点击任务，并允许在特定条件下中断当前任务
     *
     * @param gesture 点击任务的手势描述，用于执行点击操作
     * @param cancelDescription 中断手势的描述，用于在需要中断时提供中断操作的信息
     * @param interrupt 是否中断当前正在执行的任务，如果为true，则尝试中断并重新准备执行点击任务
     */
    suspend fun publishClickTask(
        gesture: GestureDescription,
        cancelDescription: GestureDescription.StrokeDescription? = null,
        interrupt: Boolean = false,
    ) {
        aService ?: return
        // 检查当前执行状态，如果状态表明任务已经失败或更糟，则直接执行点击任务
        if (runStateFlow.value >= EXECUTION_FAILED) {
            executeClick(gesture)
            this.cancelDescription = cancelDescription
        } else {
            // 如果不允许中断，或者当前任务状态不允许中断，则等待状态变为EXECUTION_FAILED或更糟后执行点击任务
            if (interrupt) {
                runStateFlow.value = PREPARING
                executeClick(gesture)
                this.cancelDescription = cancelDescription
            } else {
                // 等待状态变为EXECUTION_FAILED或更糟，然后执行点击任务
                runStateFlow
                    .first { it >= EXECUTION_FAILED }
                    .let {
                        executeClick(gesture)
                        this.cancelDescription = cancelDescription
                    }
            }
        }
    }


    /**
     * 暂停函数，用于发布点击任务
     *
     * 此函数根据当前的执行状态来决定是否执行点击任务以及如何处理中断逻辑
     * 它通过[runStateFlow]的状态来判断何时应该发布点击任务，并允许在特定条件下中断当前任务
     *
     * @param gesture 点击任务的手势描述，用于执行点击操作
     * @param cancelDescription 中断手势的描述，用于在需要中断时提供中断操作的信息
     * @param interrupt 是否中断当前正在执行的任务，如果为true，则尝试中断并重新准备执行点击任务
     * 这里会等到执行结束后才返回继续
     */
    suspend fun optClickTask(
        gesture: GestureDescription,
        cancelDescription: GestureDescription.StrokeDescription? = null,
        interrupt: Boolean = true,
    ): Boolean {
        aService ?: return false
        // 检查当前执行状态，如果状态表明任务已经失败或更糟，则直接执行点击任务
        if (runStateFlow.value >= EXECUTION_FAILED) {
            executeClick(gesture)
            this.cancelDescription = cancelDescription
        } else {
            // 如果不允许中断，或者当前任务状态不允许中断，则等待状态变为EXECUTION_FAILED或更糟后执行点击任务
            if (interrupt) {
                runStateFlow.value = PREPARING
                executeClick(gesture)
                this.cancelDescription = cancelDescription
            } else {
                // 等待状态变为EXECUTION_FAILED或更糟，然后执行点击任务
                runStateFlow
                    .first { it >= EXECUTION_FAILED }
                    .let {
                        executeClick(gesture)
                        this.cancelDescription = cancelDescription
                    }
            }
        }
        // 返回true表示点击任务已经完成
        runStateFlow.first { it >= EXECUTION_FAILED }.let {
            return true
        }
    }


    /**
     * 悬挂函数，用于执行点击操作
     *
     * 该函数使用协程的悬挂机制，允许在不阻塞线程的情况下执行点击操作它通过AccessibilityService
     * 发送一个GestureDescription对象来模拟用户点击操作
     *
     * @param aService AccessibilityService实例，用于执行手势操作
     * @param gesture 要执行的手势描述对象，包含了点击操作的具体信息
     */
    suspend fun executeClick(
        gesture: GestureDescription
    ) {
        // 在执行点击操作前，将运行状态设置为执行中
        runStateFlow.value = EXECUTING
        // 使用AccessibilityService派发手势，根据手势结果回调相应方法
        aService?.get()?.dispatchGesture(
            gesture, object : AccessibilityService.GestureResultCallback() {
                // 当手势操作被取消时调用
                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                    // 执行结束按压处理逻辑
                    overPress()
                    // 将运行状态设置为执行失败
                    runStateFlow.value = EXECUTION_FAILED
                }

                // 当手势操作完成时调用
                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    // 执行结束按压处理逻辑
                    overPress()
                    // 将运行状态设置为执行成功
                    runStateFlow.value = EXECUTION_SUCCESS
                }
            }, null
        )
    }


    private fun overPress() {
        cancelDescription?.let {
            aService?.get()?.dispatchGesture(
                GestureDescription.Builder().addStroke(it).build(), null, null
            )
        }
        cancelDescription = null
    }

}