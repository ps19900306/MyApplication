package com.yola.networklib.Api


import com.yola.networklib.BaseNetSigResult

interface LoginApi {

    suspend fun login(
        username: String,
        password: String
    ): BaseNetSigResult<String>


    suspend fun register(
        username: String,
        password: String
    ): BaseNetSigResult<String>


    suspend fun checkUserName(
        username: String
    ): BaseNetSigResult<String>


}