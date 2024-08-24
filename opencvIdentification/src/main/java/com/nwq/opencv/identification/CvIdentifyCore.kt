package com.nwq.opencv.identification

class CvIdentifyCore {

//    // 存储最近几张图像用于比较
//    private val recentImages = mutableListOf<Mat>()
//
//    private fun isSignificantChange(currentMat: Mat): Boolean {
//        // 限制最近图像的数量
//        if (recentImages.size > 3) {
//            recentImages.removeAt(0)
//        }
//
//        // 如果没有足够的图像进行比较，则认为变化显著
//        if (recentImages.size < 3) return true
//
//        // 计算与前三张图像的MSE
//        var totalMse = 0.0
//        for (recentMat in recentImages) {
//            totalMse += calculateMse(recentMat, currentMat)
//        }
//        val avgMse = totalMse / recentImages.size
//
//        // 设置阈值
//        val threshold = 100.0
//        return avgMse > threshold
//    }
}