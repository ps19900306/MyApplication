package com.yola.networklib.Api


import com.yola.networklib.BaseNetSigResult
import com.yola.networklib.bean.UserBean

interface UserApi {


   suspend  fun login(
        username: String,
        password: String
    ): BaseNetSigResult<UserBean>


    suspend fun register(
        username: String,
        password: String
    ): BaseNetSigResult<UserBean>


}