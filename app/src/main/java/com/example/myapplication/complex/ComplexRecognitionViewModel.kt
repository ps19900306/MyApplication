package com.example.myapplication.complex

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.luck.picture.lib.utils.ToastUtils
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.opencv.opt.CropAreaStep
import com.nwq.opencv.opt.MatResult
import com.nwq.opencv.opt.OptStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.opencv.core.Mat

//复杂识别 图像目标识别
class ComplexRecognitionViewModel : ViewModel() {

    private val optList = mutableListOf<MatResult>()

    private val matList = mutableListOf<Mat>()

    private val typeList = mutableListOf<Int>()

    //原始图片 GBR格式的Mat
    private var srcMat: Mat? = null

    private var lastType = OptStep.MAT_TYPE_ALL

    private var nowStep = 0;

    //用于展示最新图片的
    private val _nowBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null);
    public val nowBitmapFlow: Flow<Bitmap?> = _nowBitmapFlow

    //这个是找图范围
    private var mCropArea: CoordinateArea? = null


    public fun getGrayMat(isModify: Boolean = false): Mat? {
        if (srcMat == null) {
            return null
        }
        if (nowStep <= 0) {
            return MatUtils.rgb2Gray(srcMat!!)
        }
        val mat = matList.get(if (isModify) nowStep - 2 else nowStep - 1)
        val type = typeList.get(if (isModify) nowStep - 2 else nowStep - 1)

        when (type) {
            OptStep.MAT_TYPE_RGB -> {
                return MatUtils.rgb2Gray(mat)
            }

            OptStep.MAT_TYPE_HSV -> {
                return MatUtils.rgb2Hsv(mat)
            }

            OptStep.MAT_TYPE_GRAY -> {
                return mat
            }
        }
        return null
    }

    public fun getCropArea(): CoordinateArea? {
        return mCropArea
    }

    public fun setNewBitmap(bitmap: Bitmap) {
        srcMat = MatUtils.bitmapToMat(bitmap)
        if (optList.isEmpty()) {
            _nowBitmapFlow.tryEmit(bitmap)
        } else {
            reExecute();
        }
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


    public suspend fun modifyStep(optStep: MatResult) {
        val index = optList.indexOf(optStep)
        rereExecute(index)
    }


    //增加一部操作
    public suspend fun addOptStep(optStep: MatResult) {
        if (srcMat == null)
            return;
        if (nowStep >= optList.size) {
            optList.add(optStep)
            rereExecute(optList.size - 1)
        } else {
            optList.add(nowStep, optStep)
            rereExecute(nowStep)
        }
        nowStep++
    }

    //当移除一个选择项目时候的操作
    public suspend fun removeOptStep(optStep: MatResult) {
        val index = optList.indexOf(optStep)
        rereExecute(index)
    }


    private fun reExecute() {
        matList.clear()
        typeList.clear()
        optList.forEach {
            val (newMat, type) = it.performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) OptStep.MAT_TYPE_RGB else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            typeList.add(type)
            lastType = type
            matList.add(newMat)
        }
        sendNowBitmap(
            if (matList.isEmpty()) srcMat!! else matList.last(),
            if (typeList.isEmpty()) OptStep.MAT_TYPE_RGB else typeList.last()
        )
    }

    private fun sendNowBitmap(mat: Mat, type: Int) {
        when (type) {
            OptStep.MAT_TYPE_RGB -> {
                _nowBitmapFlow.tryEmit(MatUtils.matToBitmap(mat))
            }

            OptStep.MAT_TYPE_HSV -> {
                _nowBitmapFlow.tryEmit(MatUtils.hsvMatToBitmap(mat))
            }

            OptStep.MAT_TYPE_GRAY -> {
                _nowBitmapFlow.tryEmit(MatUtils.grayMatToBitmap(mat))
            }
        }
    }


    //这里表示从第几项开始重新操作
    private fun rereExecute(startIndex: Int) { //1
        if (startIndex >= optList.size) {
            T.show("rereExecute 下标越界")
            return
        }
        //全部从新开始做
        if (startIndex == 0) {
            reExecute()
            return
        }
        //为尾部添加的操作
        if (startIndex == (optList.size - 1)) {//修改的是最后一项
            val (newMat, type) = optList[startIndex].performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) OptStep.MAT_TYPE_RGB else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            matList.add(newMat)
            typeList.add(type)
            return
        }

        //在中间进行的操作进行的修改
        val tempMatList = matList.subList(0, startIndex) //这里开始是包含 结束是不包含
        val tempTypeList = typeList.subList(0, startIndex)
        matList.clear()
        typeList.clear()
        matList.addAll(tempMatList)
        typeList.addAll(tempTypeList)

        for (i in startIndex until optList.size) {
            val (newMat, type) = optList[i].performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) OptStep.MAT_TYPE_RGB else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            lastType = type
            matList.add(newMat)
            typeList.add(type)
        }
    }


}