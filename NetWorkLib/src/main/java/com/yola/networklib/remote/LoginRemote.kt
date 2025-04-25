package com.yola.networklib.remote

import com.yola.networklib.Api.LoginApi
import com.yola.networklib.BaseNetSigResult
import com.yola.networklib.BaseRemote

class LoginRemote(baseUrl: String) : BaseRemote<LoginApi>(baseUrl) {

    override fun getApi(): Class<LoginApi> {
        return LoginApi::class.java
    }

    suspend fun login(username: String, password: String): BaseNetSigResult<String> {
        return runOnIoAndReturnOnMain {
            api.login(username, password)
        }
    }


}