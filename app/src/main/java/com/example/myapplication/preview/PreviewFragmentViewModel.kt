package com.example.myapplication.preview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.myapplication.R
import com.example.myapplication.base.TouchOptModel

class PreviewFragmentViewModel : ViewModel() {

//    //这个是操作项目的
//    public val optList: MutableStateFlow<List<PreviewOptItem>?> = MutableStateFlow(null)

    public val optList: MutableList<PreviewOptItem> = mutableListOf()


    //这个是默认选项
    public val defaultList = listOf<PreviewOptItem>(
        PreviewOptItem(R.string.full_screen, TouchOptModel.FULL_SCREEN),
        PreviewOptItem(R.string.select_picture, TouchOptModel.SELECT_PICTURE),
    )

}