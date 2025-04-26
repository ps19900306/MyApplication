package com.yola.networklib


import android.net.http.HttpException
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yola.networklib.Interceptor.UnifiedHeaderInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException


abstract class BaseRemote<T>(val baseUrl: String) {

    abstract fun getApi(): Class<T>

    protected val api: T by lazy {
        createApi()
    }

    //
    protected fun createApi(): T {
        //Retrofit.Builder builder = Builder () //基础url
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())  // 将结果转换成一个实体类
            .addCallAdapterFactory(CoroutineCallAdapterFactory())// 将结果转换成协程
            .build()
            .create(getApi())
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

    /**
     * 通用的协程上下文切换工具函数
     * @param block 需要在 IO 线程中执行的代码块
     * @return 返回结果，并在主线程中处理
     */
    suspend fun <T> runOnIoAndReturnOnMain(block: suspend () -> BaseNetSigResult<T>): BaseNetSigResult<T> {
        // 在 IO 线程中执行代码块
        val result = withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                // 处理各种异常情况
                when (e) {
                    is SocketTimeoutException -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE_TIME_OUT)
                    }

                    is ConnectException -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE_UNKNOWN_SOCKET)
                    }

                    is SSLHandshakeException -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE_UNKNOWN_SSL)
                    }

                    is HttpException -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE)
                    }

                    is IOException -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE_IO)
                    }

                    else -> {
                        BaseNetSigResult.error<T>(ErrorCode.NETWORK_ERROR_CODE)
                    }
                }
            }
        }
        // 返回结果到主线程
        return withContext(Dispatchers.Main) {
            result
        }
    }
}