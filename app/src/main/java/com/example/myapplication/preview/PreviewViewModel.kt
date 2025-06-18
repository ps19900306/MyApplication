package com.example.myapplication.preview

import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import com.example.myapplication.base.TouchOptModel
import com.nwq.baseobj.ICoordinate
import com.nwq.baseutils.MatUtils

class PreviewViewModel : ViewModel() {

//    //这个是操作项目的
//    public val optList: MutableStateFlow<List<PreviewOptItem>?> = MutableStateFlow(null)

    public val optList: MutableList<PreviewOptItem> = mutableListOf()

    public var path: String? = null
    public var type = MatUtils.STORAGE_ASSET_TYPE


    public fun getCoordinate(key: Int): ICoordinate? {
        return optList.find { it.key == key }?.coordinate
    }

    //这个是默认选项
    public val defaultList = listOf<PreviewOptItem>(
        PreviewOptItem(R.string.full_screen, TouchOptModel.FULL_SCREEN),
        PreviewOptItem(R.string.select_picture, TouchOptModel.SELECT_PICTURE),
    )

}