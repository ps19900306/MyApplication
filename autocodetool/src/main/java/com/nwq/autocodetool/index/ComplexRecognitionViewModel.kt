package com.nwq.autocodetool.index

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.optlib.bean.SegmentMatInfo
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.GsonUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.optlib.MatResult
import com.nwq.optlib.db.bean.CropAreaDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.opencv.core.Mat

//复杂识别 图像目标识别
class ComplexRecognitionViewModel : ViewModel() {

    private val TAG = "ComplexRecognitionViewModel"
    private val optList = mutableListOf<MatResult>()

    private val matList = mutableListOf<Mat>()

    private val typeList = mutableListOf<Int>()

    //原始图片 GBR格式的Mat
    private var srcMat: Mat? = null

    private var nowStep = 0;

    //用于展示最新图片的
    private val _nowBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null);
    public val nowBitmapFlow: Flow<Bitmap?> = _nowBitmapFlow

    //这个是找图范围
    public var findArea: CoordinateArea? = null
    private var mCropArea: CoordinateArea? = null

    //分割后的参数详细间SegmentParameterDialog
    public var segmentParameter: IntArray = intArrayOf(0, 0, 0, 0, 0, 0)
    public val segmentAreaListFow = MutableStateFlow<List<SegmentMatInfo>?>(null)


    public fun getGrayMat(index: Int): Mat? {
        if (srcMat == null) {
            return null
        }
        if (nowStep <= 0) {
            return MatUtils.bgr2Gray(srcMat!!)
        }
        val mat = matList.get(if (index == -1) nowStep - 1 else index - 1)
        val type = typeList.get(if (index == -1) nowStep - 1 else index - 1)

        when (type) {
            MatUtils.MAT_TYPE_BGR -> {
                return MatUtils.bgr2Gray(mat)
            }

            MatUtils.MAT_TYPE_HSV -> {
                return MatUtils.bgr2Hsv(mat)
            }

            MatUtils.MAT_TYPE_GRAY -> {
                return mat
            }
        }
        return null
    }

    public fun getHsvMat(index: Int): Mat? {
        if (srcMat == null) {
            return null
        }
        if (nowStep <= 0) {
            return MatUtils.bgr2Gray(srcMat!!)
        }
        val mat = matList.get(if (index == -1) nowStep - 1 else index - 1)
        val type = typeList.get(if (index == -1) nowStep - 1 else index - 1)

        when (type) {
            MatUtils.MAT_TYPE_BGR -> {
                return MatUtils.bgr2Hsv(mat)
            }

            MatUtils.MAT_TYPE_HSV -> {
                return mat
            }
        }
        return null
    }



    public fun getIndex(targetClass: Class<out MatResult>): Int {
        val index = optList.indexOfFirst { targetClass.isInstance(it) }
        return index
    }

    public fun <T : MatResult> getMatResultByClass(targetClass: Class<T>): T? {
        val index = optList.indexOfFirst { targetClass.isInstance(it) }
        if (index == -1) {
            return null
        }
        return targetClass.cast(optList[index])
    }




    public fun getCropArea(): CoordinateArea? {
        return mCropArea
    }

    public fun setNewBitmap(bitmap: Bitmap) {
        srcMat = MatUtils.bitmapToMat(bitmap)
        segmentAreaListFow.tryEmit(null)
        if (optList.isEmpty()) {
            _nowBitmapFlow.tryEmit(bitmap)
        } else {
            reExecute();
        }
    }


    public fun checkAndAddOpt(optStep: MatResult, targetClass: Class<out MatResult>) {
        val index = optList.indexOfFirst { targetClass.isInstance(it) }
        when {
            index == -1 -> {
                Log.i(TAG, "checkAndAddOpt 添加新的操作")
                // 不存在该类型步骤，添加到末尾
                addOptStep(optStep)
            }

            else -> {
                // 替换现有步骤并重新执行
                Log.i(TAG, "checkAndAddOpt 执行替换操作")
                optList[index] = optStep
                rereExecute(index)
            }
        }
    }


    //当移除一个选择项目时候的操作
    public suspend fun removeOptStep(optStep: MatResult) {
        val index = optList.indexOf(optStep)
        rereExecute(index)
    }

    //增加一部操作
    private fun addOptStep(optStep: MatResult) {
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


    private fun reExecute() {
        matList.forEach {
            it.release()
        }
        matList.clear()
        typeList.clear()
        optList.forEach {
            val (newMat, type) = it.performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) MatUtils.MAT_TYPE_BGR else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            typeList.add(type)
            matList.add(newMat)
        }
        sendNowBitmap(
            if (matList.isEmpty()) srcMat!! else matList.last(),
            if (typeList.isEmpty()) MatUtils.MAT_TYPE_BGR else typeList.last()
        )
    }

    private fun sendNowBitmap(mat: Mat, type: Int) {
        when (type) {
            MatUtils.MAT_TYPE_BGR -> {
                _nowBitmapFlow.tryEmit(MatUtils.matToBitmap(mat))
            }

            MatUtils.MAT_TYPE_HSV -> {
                _nowBitmapFlow.tryEmit(MatUtils.hsvMatToBitmap(mat))
            }

            MatUtils.MAT_TYPE_GRAY, MatUtils.MAT_TYPE_THRESHOLD -> {
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

        //**其实是添加 **
        //为尾部添加的操作
        if (startIndex == (optList.size - 1)) {//修改的是最后一项
            Log.i(TAG, "添加新的操作")
            val (newMat, type) = optList[startIndex].performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) MatUtils.MAT_TYPE_BGR else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            matList.add(newMat)
            typeList.add(type)
            sendNowBitmap(newMat, type)
            return
        }

        //** 在中间进行操作 **
        //在中间进行的操作进行的修改
        Log.i(TAG, "修改操作")
        val tempMatList = matList.subList(0, startIndex) //这里开始是包含 结束是不包含
        val tempTypeList = typeList.subList(0, startIndex)
        //对资源进行释放
        for (i in startIndex until matList.size) {
            matList[i].release()
        }
        matList.clear()
        typeList.clear()
        matList.addAll(tempMatList)
        typeList.addAll(tempTypeList)

        for (i in startIndex until optList.size) {
            val (newMat, type) = optList[i].performOperations(
                if (matList.isEmpty()) srcMat!! else matList.last(),
                if (typeList.isEmpty()) MatUtils.MAT_TYPE_BGR else typeList.last()
            )
            if (newMat == null) {
                T.show("操作失败,请查看日志")
                return
            }
            matList.add(newMat)
            typeList.add(type)
        }
        sendNowBitmap(matList.last(), typeList.last())
    }


    fun mergeAndCrop() {
        if (matList.isEmpty() || srcMat == null)
            return
        val mat = matList.last()
        val rect = MatUtils.findBoundingRectForWhiteArea(mat) ?: return
        Log.i(TAG, "mergeAndCrop rect:${GsonUtils.toJson(rect)} ")
        val matResult = optList.find { it is CropAreaDb }
        if (matResult is CropAreaDb) {
            Log.i(
                TAG,
                "mergeAndCrop old:${GsonUtils.toJson(matResult.coordinateArea)}  index::${
                    optList.indexOf(matResult)
                } "
            )
            matResult.coordinateArea.x += rect.x
            matResult.coordinateArea.y += rect.y
            matResult.coordinateArea.width = rect.width
            matResult.coordinateArea.height = rect.height
            Log.i(TAG, "mergeAndCrop new:${GsonUtils.toJson(matResult.coordinateArea)} ")
        } else {
            val coordinateArea = CoordinateArea(rect.x, rect.y, rect.width, rect.height)
            Log.i(TAG, "mergeAndCrop creat:${GsonUtils.toJson(coordinateArea)} ")
            optList.add(0, CropAreaDb().apply { this.coordinateArea = coordinateArea })
        }
        reExecute()
    }

    fun addHsvRule(keyTag: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//           val autoRulePoint =
//                IdentifyDatabase.getDatabase().autoRulePointDao().findByKeyTag(keyTag)?:return@launch
//            addOptStep(BinarizationByHsvRule(autoRulePoint))
//        }
    }


    //
    fun segmentByConnectedRegion(data: IntArray) {
        if (srcMat == null || matList.isEmpty()) {
            T.show("请先选择图片")
            return
        }

        segmentParameter = data
        if (typeList.last() == MatUtils.MAT_TYPE_THRESHOLD) {
            var areaList = MatUtils.segmentImageByConnectedRegions(
                matList.last(),
                data[0],
                data[1],
                data[2],
                data[3]
            )
            if (segmentParameter[4] > 0 || segmentParameter[5] > 0) {
                areaList = MatUtils.mergeRegions(areaList, segmentParameter[4], segmentParameter[5])
            }
            val mat = matList.last()
            val resultList= areaList.map {
                val nowMat = MatUtils.cropMat(mat, it)
                val bitmap = MatUtils.grayMatToBitmap( nowMat)
                val info =  SegmentMatInfo()
                info.mMat = nowMat
                info.mBitmap = bitmap
                info.coordinateArea = it
                info
            }
            segmentAreaListFow.tryEmit(resultList)
        } else {
            T.show("只能对二值图进行分割")
        }
    }

}