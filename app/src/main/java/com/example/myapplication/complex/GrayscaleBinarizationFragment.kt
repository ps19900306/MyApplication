package com.example.myapplication.complex

import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.databinding.FragmentGrayscaleBinarizationBinding


class GrayscaleBinarizationFragment: AppToolBarFragment<FragmentGrayscaleBinarizationBinding>()  {

    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })
    override fun createBinding(inflater: LayoutInflater): FragmentGrayscaleBinarizationBinding {
        TODO("Not yet implemented")
    }

    override fun getMenuRes(): Int {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onBackPress(): Boolean {
        TODO("Not yet implemented")
    }
}