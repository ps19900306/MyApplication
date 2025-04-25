package com.yola.networklib


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yola.networklib.Interceptor.UnifiedHeaderInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


abstract class BaseRemote<T> {

    //
    protected fun createApi(baseUrl: String, service: Class<T>): T {
        //Retrofit.Builder builder = Builder () //基础url
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())  // 将结果转换成一个实体类
            .addCallAdapterFactory(CoroutineCallAdapterFactory())// 将结果转换成协程
            .build()
            .create(service)
    }





    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(UnifiedHeaderInterceptor())
            .connectionPool(ConnectionPool(10, 10, TimeUnit.MINUTES))
            .build()
    }


}