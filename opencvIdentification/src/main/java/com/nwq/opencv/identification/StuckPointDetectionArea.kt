package com.nwq.opencv.identification

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake
import org.opencv.core.Core
import org.opencv.core.Mat

//裁剪区域
//阈值
class StuckPointDetectionArea(
    val coordinateArea: CoordinateArea? = null,
    val threshold: Int = 3,
    val diffThreshold: Long = 1000L,
) : IStuckPointDetection {

    // 存储最近几张图像用于比较
    private val recentImages = mutableListOf<Mat>()


    // 检查当前帧与最近的几帧之间的差异，判断画面是否卡住
    override suspend fun checkStuckPoint(): Int {
        // 根据预设的裁剪区域从当前帧中裁剪出感兴趣的区域
        val croppedFrame = ImgTake.imgTake.getHsvMat(coordinateArea) ?: return -1
        // 如果最近的图像帧数量小于阈值，直接添加当前帧并返回帧数不足的提示
        if (recentImages.size < threshold) {
            recentImages.add(croppedFrame)
            return -1
        } else {
            // 初始化差异总和用于计算平均差异度
            var diffSum = 0.0
            // 遍历最近的每一个图像帧，计算当前帧与这些帧的差异并累加
            for (prev in recentImages) {
                val diff = Mat()
                // 计算当前帧与之前帧的差异
                Core.absdiff(croppedFrame, prev, diff)
                // 累加所有差异值
                diffSum += Core.sumElems(diff).`val`[0]
            }
            // 如果平均差异度大于设定的阈值，表示画面有变化，清除之前的图像记录并返回正常状态
            if (diffSum / recentImages.size > diffThreshold) {
                recentImages.clear()
                return 0
            } else {
                // 计算当前帧与最早记录帧之间的帧数差，用于判断画面卡住的情况
                val result = recentImages.size - threshold + 1
                recentImages.add(croppedFrame)
                return result
            }
        }
    }

    override fun resetCount() {
        recentImages.clear()
    }


}