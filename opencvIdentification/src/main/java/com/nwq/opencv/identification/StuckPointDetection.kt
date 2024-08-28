package com.nwq.opencv.identification

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect

class StuckPointDetection {

    // 存储最近几张图像用于比较
    private val recentImages = mutableListOf<Mat>()

    var diffThreshold = 1000.0 // 根据实际需求调整阈值
    //裁剪区域
    var cropRect = Rect(0, 0, 640, 480)
    //阈值
    var threshold = 3 // 最大存储数量


    /**
     * 检查当前帧是否处于静止状态
     *
     * 此函数的目的是通过比较当前视频帧与最近几个帧之间的差异来判断画面是否发生变化
     * 如果画面变化小于设定的阈值，持续一定时间后，则认为画面可能卡住
     *
     * @param currentFrame 当前的视频帧
     * @return 如果返回 -1，则表示当前帧数量不足以进行比较；如果返回大于等于0的值，则表示已经比较了的帧数，
     *         用于判断画面是否卡住（通过与阈值比较）
     */
    private fun checkStuckPoint(currentFrame: Mat): Int {
        // 根据预设的裁剪区域从当前帧中裁剪出感兴趣的区域
        val croppedFrame = currentFrame.submat(cropRect)
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
                Core.absdiff(currentFrame, prev, diff)
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



}