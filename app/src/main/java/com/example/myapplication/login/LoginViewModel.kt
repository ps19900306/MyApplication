package com.example.myapplication.login

import android.os.Build
import androidx.lifecycle.ViewModel
import com.example.myapplication.BuildConfig
import com.luck.picture.lib.utils.ToastUtils
import com.yola.networklib.remote.LoginRemote


class LoginViewModel : ViewModel() {

    private val mLoginRemote by lazy {
        LoginRemote(BuildConfig.BASE_URL)
    }

    private suspend fun login(username: String, password: String): Boolean {
        val result = mLoginRemote.login(username, password)
        if (result.isSuccess()) {
            return true
        } else {
            return false
        }
    }


}