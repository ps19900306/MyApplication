package com.example.myapplication.complex

import android.graphics.Bitmap
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.opt.MatResult
import com.nwq.opencv.opt.OptStep
import org.opencv.core.Mat

//复杂识别 图像目标识别
class ComplexRecognitionViewModel {

    private val optList = mutableListOf<MatResult>()

    private val matList = mutableListOf<Mat>()

    private var srcMat: Mat? = null

    private var lastMatType: Int = -1


    public fun setBitmap(bitmap: Bitmap) {
        srcMat = MatUtils.bitmapToMat(bitmap)
        MatUtils.bitmapToHsvMat()
    }


    //增加一部操作
    public suspend fun addOptStep(optStep: MatResult) {
        if (srcMat == null)
            return;
        val newMat = optStep.performOperations(if (matList.isEmpty()) srcMat!! else matList.last())
        matList.add(newMat)
        optList.add(optStep)
    }

    //removeOptStep
    public suspend fun removeOptStep(optStep: MatResult) {
        val index = optList.indexOf(optStep)  //4   5
        if (index > 0) {
            optList.removeAt(index)   //4   5
            val list = matList.subList(0, index)//0-3   4
            matList.clear()
            matList.addAll(list)
            for (i in index until optList.size) {
                // 确保matList不为空再访问last()
                val newMat = optList[i].performOperations(matList.last())
                matList.add(newMat)
            }
        } else if (index == 0) {
            optList.removeAt(index)
            reExecute();
        }
    }


    private fun reExecute() {
        matList.clear()
        for (i in optList.indices) {
            val newMat =
                optList[i].performOperations(if (matList.isEmpty()) srcMat!! else matList.last())
            matList.add(newMat)
        }
    }

}