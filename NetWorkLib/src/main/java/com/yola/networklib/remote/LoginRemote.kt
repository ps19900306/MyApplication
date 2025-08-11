package com.yola.networklib.remote

import com.yola.networklib.api.LoginApi
import com.yola.networklib.BaseNetSigResult
import com.yola.networklib.BaseRemote
import com.yola.networklib.Interceptor.UnifiedHeaderInterceptor

class LoginRemote() : BaseRemote<LoginApi>() {

    override fun getApi(): Class<LoginApi> {
        return LoginApi::class.java
    }

    suspend fun login(username: String, password: String): BaseNetSigResult<String> {
        return runOnIoAndReturnOnMain {
            val result = api.login(username, password)
            if (result.isSuccessNoNull()) {
                UnifiedHeaderInterceptor.token = result.data!!
            }
            result
        }
    }


    suspend fun register(
        username: String,
        password: String,
        password2: String
    ): BaseNetSigResult<String> {
        return runOnIoAndReturnOnMain {
            api.register(username, password, password2)
        }
    }

    suspend fun checkUserName(username: String): BaseNetSigResult<String> {
        return runOnIoAndReturnOnMain {
            api.checkUserName(username)
        }
    }


}