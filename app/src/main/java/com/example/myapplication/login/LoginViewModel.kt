package com.example.myapplication.login


import androidx.lifecycle.ViewModel
import com.example.myapplication.BuildConfig
import com.nwq.baseutils.T
import com.yola.networklib.remote.LoginRemote


class LoginViewModel : ViewModel() {

    private val mLoginRemote by lazy {
        LoginRemote(BuildConfig.BASE_URL)
    }

    private suspend fun login(username: String, password: String): Boolean {
        val result = mLoginRemote.login(username, password)
        if (result.isSuccess()) {
            T.show("登录成功")
            return true
        } else {
            T.show("失败:"+result.code)
            return false
        }
    }


}