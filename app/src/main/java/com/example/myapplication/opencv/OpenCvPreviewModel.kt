package com.example.myapplication.opencv

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ColorItem
import com.nwq.baseutils.ByteToIntUtils
import com.nwq.baseutils.MatUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.opencv.core.Mat

//主要进行预览操作相关的
class OpenCvPreviewModel : ViewModel() {

    private val TAG = OpenCvPreviewModel::class.java.simpleName

    // 原始图片
    public var srcBitmap: Bitmap? = null;

    //
    private var srcMat: Mat? = null;

    //
    private var srcHSVMat: Mat? = null;

    // 显示的图片
    private var showBitmap: Bitmap? = null;
    val expendRange = 3;

    public var showBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    public val colorsList = MutableStateFlow(listOf<ColorItem>())


    fun setScrMap(it: Bitmap) {
        srcBitmap = it;
        showBitmapFlow.value = it
        srcMat = null
        srcHSVMat = null
    }

    private val _HFlow =
        MutableStateFlow<Int>(
            ByteToIntUtils.bytesToInt(
                byteArrayOf(
                    -128,
                    52,
                    0,
                    0
                )
            )
        ).apply { debounce(1000) }     //52= 180-128
    private val _SFlow =
        MutableStateFlow<Int>(
            ByteToIntUtils.bytesToInt(
                byteArrayOf(
                    -128,
                    127,
                    0,
                    0
                )
            )
        ).apply { debounce(1000) }//127= 255-128
    private val _VFlow =
        MutableStateFlow<Int>(
            ByteToIntUtils.bytesToInt(
                byteArrayOf(
                    -128,
                    127,
                    0,
                    0
                )
            )
        ).apply { debounce(1000) } //127= 255-128


    // Combine the parameters and get the Flow from getLogFlow
    val processHsvBitmapFlow: Flow<Bitmap?> = combine(
        _HFlow,
        _SFlow,
        _VFlow,
    ) { h, s, v ->
        val bitmap = getOrCreateShowBitmap(
            ByteToIntUtils.getByteFromInt2(h, 0), ByteToIntUtils.getByteFromInt2(h, 1),
            ByteToIntUtils.getByteFromInt2(s, 0), ByteToIntUtils.getByteFromInt2(s, 1),
            ByteToIntUtils.getByteFromInt2(v, 0), ByteToIntUtils.getByteFromInt2(v, 1)
        )
        bitmap
    }


    private fun getOrCreateShowBitmap(
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Bitmap? {
        updateColorsList(minH, maxH, minS, maxS, minV, maxV)
        val bitmap = if (getOrCreateHsvMat() == null) {
            null
        } else {
            val hsvMat = getOrCreateHsvMat()!!
            val maskMat = MatUtils.filterByHsv(hsvMat, minH, maxH, minS, maxS, minV, maxV)
            MatUtils.hsvMatToBitmap(maskMat)
        }
        return bitmap
    }

    private fun updateColorsList(
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ) {
        val list = mutableListOf<ColorItem>()
        // 生成所有组合
        list.add(ColorItem(floatArrayOf(minH.toFloat(), maxS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), maxS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(minH.toFloat(), minS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), minS.toFloat(), maxV.toFloat())))

        list.add(ColorItem(floatArrayOf(minH.toFloat(), maxS.toFloat(), minV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), maxS.toFloat(), minV.toFloat())))

        list.add(ColorItem(floatArrayOf(minH.toFloat(), minS.toFloat(), minV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), minS.toFloat(), minV.toFloat())))


        colorsList.value = list
    }


    private var job: Job? = null
    private fun initSrcJob() {
        if (job == null) {
            job = viewModelScope.launch {
                processHsvBitmapFlow.collect {
                    showBitmapFlow.value = it
                }
            }
        }
    }


    fun upDataHFlow(p: Int) {
        val temp = ByteToIntUtils.setByteToInt2(
            _HFlow.value,
            0,
            if (p > expendRange) p - expendRange else 0
        )
        _HFlow.value =
            ByteToIntUtils.setByteToInt2(temp, 1, if (p < 180 - expendRange) p + expendRange else 0)
        initSrcJob();
    }


    fun upDataMinHFlow(minH: Int) {
        _HFlow.value = ByteToIntUtils.setByteToInt2(_HFlow.value, 0, minH)
        initSrcJob();
    }

    fun getMinH(): Int {
        return ByteToIntUtils.getByteFromInt2(_HFlow.value, 0)
    }

    fun upDataMaxHFlow(maxH: Int) {
        _HFlow.value = ByteToIntUtils.setByteToInt2(_HFlow.value, 1, maxH)
        initSrcJob();
    }

    fun getMaxH(): Int {
        return ByteToIntUtils.getByteFromInt2(_HFlow.value, 1)
    }

    fun upDataMinSFlow(minS: Int) {
        _SFlow.value = ByteToIntUtils.setByteToInt2(_SFlow.value, 0, minS)
        initSrcJob();
    }

    fun getMinS(): Int {
        return ByteToIntUtils.getByteFromInt2(_SFlow.value, 0)
    }

    fun upDataMaxSFlow(maxS: Int) {
        _SFlow.value = ByteToIntUtils.setByteToInt2(_SFlow.value, 1, maxS)
        initSrcJob();
    }

    fun getMaxS(): Int {
        return ByteToIntUtils.getByteFromInt2(_SFlow.value, 1)
    }

    fun upDataMinVFlow(minV: Int) {
        _VFlow.value = ByteToIntUtils.setByteToInt2(_VFlow.value, 0, minV)
        initSrcJob();
    }

    fun getMinV(): Int {
        return ByteToIntUtils.getByteFromInt2(_VFlow.value, 0)
    }

    fun upDataMaxVFlow(maxV: Int) {
        _VFlow.value = ByteToIntUtils.setByteToInt2(_VFlow.value, 1, maxV)
        initSrcJob();
    }

    fun getMaxV(): Int {
        return ByteToIntUtils.getByteFromInt2(_VFlow.value, 1)
    }

    private fun getOrCreateSrcMat(): Mat? {
        return if (srcMat == null) {
            if (srcBitmap != null) {
                srcMat = MatUtils.bitmapToMat(srcBitmap!!)
                srcMat
            } else {
                null
            }
        } else {
            srcMat
        }
    }

    private fun getOrCreateHsvMat(): Mat? {
        return if (srcHSVMat == null) {
            if (srcBitmap != null) {
                srcHSVMat = MatUtils.bitmapToHsvMat(srcBitmap!!)
                srcHSVMat
            } else {
                null
            }
        } else {
            srcHSVMat
        }
    }
}