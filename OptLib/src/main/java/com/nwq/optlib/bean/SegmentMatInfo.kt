package com.nwq.optlib.bean

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.simplelist.ICheckText
import org.opencv.core.Mat

//切分的一个单独的Mat信息
class SegmentMatInfo : ICheckText<SegmentMatInfo> {

    var flagStr: String? = null

    //不在Ui上显示
    var mMat: Mat? = null

    var mBitmap: Bitmap? = null

    //显示toString信息
    lateinit var coordinateArea: CoordinateArea


    var isCheck: Boolean = false

    override fun setCheckStatus(boolean: Boolean) {
        isCheck = boolean
    }

    override fun isCheckStatus(): Boolean {
        return isCheck
    }

    override fun getText(): String {
        return coordinateArea?.toStringSimple() ?: ""
    }

    override fun getT(): SegmentMatInfo {
        return this
    }
}