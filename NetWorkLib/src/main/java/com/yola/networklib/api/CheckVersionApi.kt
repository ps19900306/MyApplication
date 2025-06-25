package com.yola.networklib.api


import com.yola.networklib.BaseNetSigResult
import com.yola.networklib.bean.VersionInfo
import retrofit2.http.POST


interface CheckVersionApi {

    @POST("/api/version/check")
    suspend fun checkVersion(
        clientVersion: String,
    ): BaseNetSigResult<VersionInfo>


}