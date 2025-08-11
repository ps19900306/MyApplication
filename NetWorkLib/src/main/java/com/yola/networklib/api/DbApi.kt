package com.yola.networklib.api

import com.yola.networklib.BaseNetSigResult
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

interface DbApi {

    /**
     * 下载远程数据库文件。
     *
     * @return 返回包含数据库文件内容的响应体。
     */
    @Streaming
    @GET("/api/db/download")
    suspend fun downloadDatabase(dbName: String): BaseNetSigResult<ResponseBody>

    /**
     * 上传本地数据库文件到服务器。
     *
     * @param databaseFile 使用 [MultipartBody.Part] 包装的数据库文件。
     * @return 返回上传结果。
     */
    @Streaming
    @POST("/api/db/upload")
    suspend fun uploadDatabase(
        @Part databaseFile: MultipartBody.Part
    ): BaseNetSigResult<String>
}