package com.yola.networklib.remote

import com.yola.networklib.BaseNetSigResult
import com.yola.networklib.BaseRemote
import com.yola.networklib.api.DbApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

class DbRemote() : BaseRemote<DbApi>() {

    override fun getApi(): Class<DbApi> {
        return DbApi::class.java
    }


    suspend fun downloadDatabaseFile(dbName: String): BaseNetSigResult<ResponseBody> {
        return api.downloadDatabase(dbName)
    }

    suspend fun uploadDatabaseFile(file: File): BaseNetSigResult<String> {
        val requestFile = RequestBody.create(MediaType.get("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return runOnIoAndReturnOnMain {
            api.uploadDatabase(body)
        }
    }
}