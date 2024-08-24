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
     * 该函数通过对比当前帧与最近帧的差异，来判断画面是否静止不动
     * 主要用于检测摄像头画面是否停滞
     *
     * @param currentFrame 当前的图像帧，使用Mat对象表示
     * @return 返回一个整型值，表示当前帧的静止状态
     */
    private fun checkStuckPoint(currentFrame: Mat): Int {
        // 根据预设的裁剪区域从当前帧中裁剪出感兴趣的区域
        val croppedFrame = currentFrame.submat(cropRect)
        // 如果最近的图像帧数量小于阈值，直接添加当前帧并返回帧数不足的提示
        if (recentImages.size < threshold) {
            recentImages.add(croppedFrame)
            return StuckStatus.NUMBER_TOO_SMALL
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
                return StuckStatus.NORMAL
            } else {
                // 根据最近图像的数量返回不同的静止状态
                val result = if (recentImages.size <= threshold) {
                    StuckStatus.IS_STUCK
                } else if (recentImages.size <= threshold * 3) {
                    StuckStatus.IS_STUCK_LONG
                } else {
                    StuckStatus.ERROR
                }
                // 添加当前帧到最近图像列表中，并返回相应的静止状态
                recentImages.add(croppedFrame)
                return result
            }
        }
    }
}