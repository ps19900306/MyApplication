package com.example.myapplication.opencv

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.nwq.baseutils.ByteToIntUtils
import com.nwq.baseutils.MatUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.opencv.core.Mat

class OpenCvOptModel : ViewModel() {


    // 图片
    private var srcBitmap: Bitmap? = null;

    //
    private var srcMat: Mat? = null;

    // 显示的图片
    private var showBitmap: Bitmap? = null;


    private val _HFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(0, 52, 0, 0)))//52= 180-128
    private val _SFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(0, 127, 0, 0)))//127= 255-128
    private val _VFlow =
        MutableStateFlow<Int>(ByteToIntUtils.bytesToInt(byteArrayOf(0, 127, 0, 0)))//127= 255-128


    // Combine the parameters and get the Flow from getLogFlow
    val logs: Flow<Bitmap?> = combine(
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
        val bitmap = if (getOrCreateSrcMat() == null) {
            null
        } else {
            val hsvMat = getOrCreateSrcMat()!!
            val maskMat = MatUtils.getMaskMat(hsvMat, minH, maxH, minS, maxS, minV, maxV)
            MatUtils.matToBitmap(maskMat)
        }
        return bitmap
    }


    fun upDataMinHFlow(minH: Int) {
        ByteToIntUtils.setByteToInt2(_HFlow.value, 0, minH)
    }

    fun upDataMaxHFlow(maxH: Int) {
        ByteToIntUtils.setByteToInt2(_HFlow.value, 1, maxH)
    }

    fun upDataMinSFlow(minS: Int) {
        ByteToIntUtils.setByteToInt2(_SFlow.value, 0, minS)
    }

    fun upDataMaxSFlow(maxS: Int) {
        ByteToIntUtils.setByteToInt2(_SFlow.value, 1, maxS)
    }

    fun upDataMinVFlow(minV: Int) {
        ByteToIntUtils.setByteToInt2(_VFlow.value, 0, minV)
    }

    fun upDataMaxVFlow(maxV: Int) {
        ByteToIntUtils.setByteToInt2(_VFlow.value, 1, maxV)
    }


    private fun getOrCreateSrcMat(): Mat? {
        return if (srcMat == null) {
            if (srcBitmap != null) {
                MatUtils.bitmapToMat(srcBitmap!!)
            } else {
                null
            }
        } else {
            srcMat
        }
    }


}