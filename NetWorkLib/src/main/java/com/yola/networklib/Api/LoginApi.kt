package com.yola.networklib.Api


import com.yola.networklib.BaseNetSigResult
import retrofit2.http.POST


interface LoginApi {

    @POST("user/info")
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