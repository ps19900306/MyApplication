package com.example.myapplication.complex

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.opt.CropAreaStep
import com.nwq.opencv.opt.MatResult
import com.nwq.opencv.opt.OptStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.opencv.core.Mat

//复杂识别 图像目标识别
class ComplexRecognitionViewModel {

    private val optList = mutableListOf<MatResult>()

    private val matList = mutableListOf<Mat>()

    //原始图片 GBR格式的Mat
    private var srcMat: Mat? = null

    private var lastType = OptStep.MAT_TYPE_ALL

    //用于展示最新图片的
    private val _nowBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null);
    public val nowBitmapFlow: Flow<Bitmap?> = _nowBitmapFlow

    //这个是找图范围
    private var mCropArea: CoordinateArea? = null


    public fun setNewBitmap(bitmap: Bitmap) {
        srcMat = MatUtils.bitmapToMat(bitmap)
        lastType = OptStep.MAT_TYPE_RGB
        _nowBitmapFlow.tryEmit(bitmap)
        reExecute();
    }


    //注意裁剪区域必定是在获取截图第一步就处理了
    public fun setCropArea(cropArea: CoordinateArea) {
        mCropArea = cropArea
        if (!optList.isEmpty() && (optList[0] is CropAreaStep)) {
            optList.removeAt(0)
        }
        optList.add(CropAreaStep(cropArea))
        reExecute()
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
        optList.forEach {
            val newMat =
                it.performOperations(if (matList.isEmpty()) srcMat!! else matList.last())
            matList.add(newMat)
        }
    }

}