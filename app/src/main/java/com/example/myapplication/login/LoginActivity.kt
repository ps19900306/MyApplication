package com.example.myapplication.login

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.opencv.OpenCvPreviewModel
import com.nwq.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel by viewModels<LoginViewModel>()


    override fun initData() {

    }

    override fun createBinding(inflater: LayoutInflater): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(inflater);
    }


}