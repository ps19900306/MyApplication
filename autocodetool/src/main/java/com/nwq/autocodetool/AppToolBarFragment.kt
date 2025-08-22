package com.nwq.autocodetool


import androidx.viewbinding.ViewBinding
import com.nwq.base.BaseToolBar2Fragment

abstract class AppToolBarFragment<VB : ViewBinding> : BaseToolBar2Fragment<VB>() {



    fun fullScreen(){
        val ac = requireActivity()
        if (ac is NavigationToolBarActivity) {
            ac.fullScreen()
        }
    }

    fun fullScreenHasTool(){
        val ac = requireActivity()
        if (ac is NavigationToolBarActivity) {
            ac.fullScreenHasTool()
        }
    }
}