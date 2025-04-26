package com.yola.networklib.Api


import com.yola.networklib.BaseNetSigResult
import retrofit2.http.POST


interface LoginApi {

    @POST("/api/auth/login")
    suspend fun login(
        username: String,
        password: String
    ): BaseNetSigResult<String>

    @POST("/api/auth/register")
    suspend fun register(
        username: String,
        password: String,
        password2: String,
    ): BaseNetSigResult<String>

    @POST("/api/auth/check")
    suspend fun checkUserName(
        username: String
    ): BaseNetSigResult<String>


}