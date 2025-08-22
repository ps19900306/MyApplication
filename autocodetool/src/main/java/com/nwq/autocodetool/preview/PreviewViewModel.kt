package com.nwq.autocodetool.preview

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.nwq.base.TouchOptModel
import com.nwq.baseobj.ICoordinate
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.MatUtils
import org.opencv.core.Mat

class PreviewViewModel : ViewModel() {

//    //这个是操作项目的
//    public val optList: MutableStateFlow<List<PreviewOptItem>?> = MutableStateFlow(null)

    public val optList: MutableList<PreviewOptItem> = mutableListOf()
    public var mBitmap: Bitmap? = null
    public val defaultAreaList = mutableListOf<PreviewCoordinateData>()
    private var mMat: Mat? = null
    public fun getSrcMat(): Mat? {
        if (mMat != null)
            return mMat
        mBitmap?.let {
            mMat = MatUtils.bitmapToHsvMat(it)
        }
        return mMat
    }

    public fun getCoordinate(key: Int): ICoordinate? {
        return optList.find { it.key == key }?.coordinate
    }

    //这个是默认选项
    public val defaultList = listOf<PreviewOptItem>(
        PreviewOptItem(R.string.full_screen, TouchOptModel.FULL_SCREEN),
      //  PreviewOptItem(R.string.select_picture, TouchOptModel.SELECT_PICTURE),
    )



}