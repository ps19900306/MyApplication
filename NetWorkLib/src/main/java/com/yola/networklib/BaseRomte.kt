package com.yola.networklib

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

abstract class BaseRemote<T> {

    //
    protected fun createApi(baseUrl:String,service: Class<T>): T {
        //Retrofit.Builder builder = Builder () //基础url
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())//
            .build()
            .create(service)
    }



}