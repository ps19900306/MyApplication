package com.example.myapplication.opencv

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.ByteToIntUtils
import com.nwq.baseutils.MatUtils
import com.nwq.loguitls.L
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.opencv.core.Mat

class OpenCvOptModel : ViewModel() {

    private val TAG =OpenCvOptModel::class.java.simpleName

    // 图片
    private var srcBitmap: Bitmap? = null;

    //
    private var srcMat: Mat? = null;

    //
    private var srcHSVMat: Mat? = null;

    // 显示的图片
    private var showBitmap: Bitmap? = null;


    public var showBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null)

    fun setScrMap(it: Bitmap) {
        srcBitmap = it;
        showBitmapFlow.value = it
        srcMat = null
        srcHSVMat = null
    }

    private val _HFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(-128, 52, 0, 0)))//52= 180-128
    private val _SFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(-128, 127, 0, 0)))//127= 255-128
    private val _VFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(-128, 127, 0, 0)))//127= 255-128


    // Combine the parameters and get the Flow from getLogFlow
    val processHsvBitmapFlow: Flow<Bitmap?> = combine(
        _HFlow,
        _SFlow,
        _VFlow,
    ) { h, s, v ->
        val bitmap = getOrCreateShowBitmap(
            ByteToIntUtils.getByteFromInt2(h, 0), ByteToIntUtils.getByteFromInt2(h, 1),
            ByteToIntUtils.getByteFromInt2(s, 0), ByteToIntUtils.getByteFromInt2(v, 1),
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
        Log.i(TAG,"getOrCreateShowBitmap :: $minH");
        val bitmap = if (getOrCreateHsvMat() == null) {
            null
        } else {
            val hsvMat = getOrCreateHsvMat()!!
            val maskMat = MatUtils.filterByHsv(hsvMat, minH, maxH, minS, maxS, minV, maxV)
            MatUtils.hsvMatToBitmap(maskMat)
        }
        return bitmap
    }


    fun upDataMinHFlow(minH: Int) {
        _HFlow.value = ByteToIntUtils.setByteToInt2(_HFlow.value, 0, minH)
    }

    fun getMinH(): Int {
        return ByteToIntUtils.getByteFromInt2(_HFlow.value, 0)
    }

    fun upDataMaxHFlow(maxH: Int) {
        _HFlow.value = ByteToIntUtils.setByteToInt2(_HFlow.value, 1, maxH)
    }

    fun getMaxH(): Int {
        return ByteToIntUtils.getByteFromInt2(_HFlow.value,1)
    }

    fun upDataMinSFlow(minS: Int) {
        _SFlow.value = ByteToIntUtils.setByteToInt2(_SFlow.value, 0, minS)
    }

    fun getMinS(): Int {
        return ByteToIntUtils.getByteFromInt2(_SFlow.value, 0)
    }

    fun upDataMaxSFlow(maxS: Int) {
        _SFlow.value = ByteToIntUtils.setByteToInt2(_SFlow.value, 1, maxS)
    }

    fun getMaxS(): Int {
        return ByteToIntUtils.getByteFromInt2(_SFlow.value,1)
    }

    fun upDataMinVFlow(minV: Int) {
        _VFlow.value = ByteToIntUtils.setByteToInt2(_VFlow.value, 0, minV)
    }

    fun getMinV(): Int {
        return ByteToIntUtils.getByteFromInt2(_VFlow.value, 0)
    }

    fun upDataMaxVFlow(maxV: Int) {
        _VFlow.value = ByteToIntUtils.setByteToInt2(_VFlow.value, 1, maxV)
    }

    fun getMaxV(): Int {
        return ByteToIntUtils.getByteFromInt2(_VFlow.value,1)
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