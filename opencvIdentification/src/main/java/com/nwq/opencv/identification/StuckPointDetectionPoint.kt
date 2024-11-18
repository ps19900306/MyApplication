package com.nwq.opencv.identification

import com.nwq.baseobj.CoordinatePoint
import com.nwq.imgtake.ImgTake
import kotlin.math.abs


class StuckPointDetectionPoint(
    val pointList: List<CoordinatePoint>,
    val diffThreshold: Double = 1.0
) : IStuckPointDetection {


    // 存储最近几张图像用于比较
    private var recentList: List<Double>? = null
    private var noChangeCount = 0

    /**
     * 检查是否存在卡顿情况的函数
     * 通过比较连续帧之间特定点的颜色差异来判断是否存在卡顿
     *
     * @return 如果没有检测到卡顿，返回0；如果检测到卡顿，返回递增的计数
     */
    override suspend fun checkStuckPoint(): Int {
        // 获取处理后的图像帧，如果获取失败，则认为可能存在卡顿，递增计数
        val croppedFrame = ImgTake.imgTake.getHsvMat() ?: return ++noChangeCount

        // 初始化列表，用于存储当前帧特定点的HSV值
        val nowList = mutableListOf<Double>()

        // 遍历每个检测点，获取并记录当前帧这些点的HSV值
        pointList.forEachIndexed { index, point ->
            nowList.add(croppedFrame[point.y, point.x][0])
        }

        // 如果这是第一次进行检测，则初始化recentList，并认为没有卡顿
        if (recentList == null) {
            recentList = nowList
            return 0
        }

        // 计算当前帧与上一帧检测点HSV值的差异
        val diffList = nowList.zip(recentList!!).map { (a, b) -> abs(a - b) }
        // 计算所有差异的总和
        val diffSum = diffList.sum()

        // 如果差异总和大于阈值，则认为没有卡顿，重置计数
        // 否则，认为存在卡顿，递增计数
        return if (diffSum > diffList.size * diffThreshold) {
            0
        } else {
            ++noChangeCount
        }
    }

    override fun resetCount() {
        noChangeCount = 0
    }


}