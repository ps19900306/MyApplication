package com.yola.networklib.Interceptor

import okhttp3.Interceptor
import okhttp3.Response

//增加统一的头文件
class UnifiedHeaderInterceptor : Interceptor {

    companion object{
         public var token = ""
    }



    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }

}