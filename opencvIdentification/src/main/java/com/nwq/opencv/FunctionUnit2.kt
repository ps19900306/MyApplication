package com.nwq.opencv


import android.util.Log
import com.nwq.constant.ConstantTime.screenshotInterval
import com.nwq.imgtake.ImgTake
import com.nwq.loguitls.L
import com.nwq.opencv.identification.IStuckPointDetection
import kotlinx.coroutines.delay

//这是一个功能模块 根据实际情况划分粒度
abstract class FunctionUnit2 {

//    companion object {
//        const val DEFAULT_MAX_COUNT = 20 * 60 //默认的最大执行次数
//
//        const val NORMAL_END = 1
//        const val STUCK_POINT_END = 1
//        const val STUCK_IMG_END = 3
//        const val TIME_END = 4
//
//    }
//
//    abstract val TAG: String
//    abstract val logicUnitList: MutableList<ILogicUnit>
//    abstract val maxCount: Int
//    protected var lastILogicUnit: ILogicUnit? = null
//
//
//    /**
//     * 启动主功能函数，使用协程实现异步操作
//     * 此函数负责循环执行主要逻辑，包括截图、卡点检测、逻辑单元处理等
//     */
//    suspend fun startFunction() {
//        // 执行启动功能前的准备工作
//        L.i(TAG, "startFunction")
//        beforeStartFunction()
//
//        // 初始化计数器为最大值
//        var count = maxCount
//
//        // 获取卡点检测实例，用于后续的卡点检测
//        val stuckPointDetection = getIStuckPointDetection()
//
//        // 主循环，根据条件判断是否继续执行
//        do {
//            // 根据设定的间隔时间暂停协程，模拟截图间隔
//            delay(screenshotInterval)
//
//            // 如果卡点检测实例存在，则执行卡点检测逻辑
//            stuckPointDetection?.let {
//                val stuckCount = it.checkStuckPoint()
//                // 如果检测到卡点，则结束功能执行，并返回
//                if (isPointDetection(stuckCount)) {
//                    L.i(TAG, "endFunction STUCK_POINT_END")
//                    endFunction(STUCK_POINT_END)
//                    return
//                }
//            }
//
//            // 查找当前符合条件的逻辑单元
//            val nowILogicUnit = logicUnitList.find { it.jude() }
//
//            // 如果上一个逻辑单元存在，则根据当前逻辑单元是否变化执行相应的逻辑
//            if (lastILogicUnit != null) {
//                if (nowILogicUnit != lastILogicUnit) {
//                    // 如果当前逻辑单元发生变化，则通知上一个逻辑单元变化，并初始化当前逻辑单元
//                    lastILogicUnit?.hasChanged(logicUnitList)
//                    nowILogicUnit?.onJude(logicUnitList, 0)
//                } else {
//                    // 如果当前逻辑单元未发生变化，并且当前逻辑单元的判断结果为真，则结束功能执行
//                    if (nowILogicUnit?.onJude(logicUnitList, ++count) == true) {
//                        L.i(TAG, "endFunction STUCK_IMG_END ${nowILogicUnit.getTag()}")
//                        endFunction(STUCK_IMG_END)
//                        return
//                    }
//                }
//                // 更新上一个逻辑单元为当前逻辑单元
//                lastILogicUnit = nowILogicUnit
//            } else {
//                // 如果上一个逻辑单元不存在，则初始化计数器，并执行当前逻辑单元的判断逻辑
//                count = 0
//                nowILogicUnit?.onJude(logicUnitList, count)
//            }
//
//            // 如果当前逻辑单元存在，并且其执行结束条件为真，则结束功能执行
//            nowILogicUnit?.let {
//                if (it.isEnd()) {
//                    endFunction(NORMAL_END)
//                    L.i(TAG, "endFunction NORMAL_END ${nowILogicUnit.getTag()}")
//                    return
//                }
//            }
//        } while (count > 0) // 循环条件判断，计数器大于0时继续循环
//
//        // 如果循环结束，则以时间结束条件结束功能执行
//        L.i(TAG, "endFunction TIME_END")
//        endFunction(TIME_END)
//    }
//
//
//    open suspend fun updateImg() {
//        ImgTake.imgTake.takeScreenImg()
//    }
//
//    open suspend fun getIStuckPointDetection(): IStuckPointDetection? {
//        return null
//    }
//
//    open suspend fun isPointDetection(count: Int): Boolean {
//        return false
//    }
//
//    open suspend fun beforeStartFunction() {
//
//    }
//
//    open suspend fun endFunction(int: Int) {
//        L.flushLogs()
//    }


}