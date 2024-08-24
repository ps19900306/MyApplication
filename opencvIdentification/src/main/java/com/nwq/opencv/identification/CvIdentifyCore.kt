package com.nwq.opencv.identification

import org.opencv.core.Core
import org.opencv.core.Mat

class CvIdentifyCore {

    // 存储最近几张图像用于比较
    private val recentImages = mutableListOf<Mat>()
    private val diffThreshold = 1000.0 // 根据实际需求调整阈值


    private fun shouldDetect(currentFrame: Mat): Boolean {
        if (recentImages.size < 3) return true

        var diffSum = 0.0
        for (prev in recentImages) {
            val diff = Mat()
            Core.absdiff(currentFrame, prev, diff)
            diffSum += Core.sumElems(diff).`val`[0]
        }

        return diffSum / recentImages.size > diffThreshold
    }



}