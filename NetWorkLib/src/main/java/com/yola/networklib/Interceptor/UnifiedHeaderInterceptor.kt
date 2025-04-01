package com.yola.networklib.Interceptor

import okhttp3.Interceptor
import okhttp3.Response

//增加统一的头文件
class UnifiedHeaderInterceptor : Interceptor {



    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // 构建新的请求
        val newRequest = originalRequest.newBuilder()
            .header("App-Version", "213231")
            .header("App-Type", "yola") // 根据实际情况设置
            .header("App-Language",  "en")
            .build()
        return chain.proceed(newRequest)
    }
}