package com.example.myapplication.complex

import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.databinding.FragmentGrayscaleBinarizationBinding
import org.opencv.core.Mat


class GrayscaleBinarizationFragment : AppToolBarFragment<FragmentGrayscaleBinarizationBinding>() {

    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })
    private val grayMat: Mat by lazy {
        viewModel.nowBitmapFlow


    }

    override fun createBinding(inflater: LayoutInflater): FragmentGrayscaleBinarizationBinding {
        return FragmentGrayscaleBinarizationBinding.inflate(inflater)
    }

    //实际并不会使用 只是保持格式
    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }

    //实际并不会使用 只是保持格式
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return true
    }

    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }

    override fun initView() {
        super.initView()
    }
}