package com.yola.networklib

object ErrorCode {


//    is SocketTimeoutException -> {
//        println("请求超时: ${e.message}")
//        // 可以在这里处理超时逻辑，如重试或显示提示
//    }
//    is ConnectException -> {
//        println("连接失败: ${e.message}")
//        // 可能是网络不可用或服务器不可达
//    }
//    is SSLHandshakeException -> {
//        println("SSL握手失败: ${e.message}")
//        // 可能是证书问题
//    }
//    is HttpException -> {
//        println("HTTP异常: ${e.message}")
//        // 其他HTTP相关异常
//    }
//    is IOException -> {
//        println("网络IO异常: ${e.message}")
//        // 其他网络IO问题
//    }
//    else -> {
//        println("未知异常: ${e.message}")
//        // 其他未预料到的异常
//    }


    const val NETWORK_ERROR_CODE=-10001;// "网络错误"
    const val NETWORK_ERROR_CODE_TIME_OUT=-10002;
    const val NETWORK_ERROR_CODE_UNKNOWN_HOST=-10004;
    const val NETWORK_ERROR_CODE_UNKNOWN_SOCKET=-10005;
    const val NETWORK_ERROR_CODE_UNKNOWN_SSL=-10006;
    const val NETWORK_ERROR_CODE_IO=-10007;




    fun getErrorCode(code: Int): String {
        return when (code) {
            NETWORK_ERROR_CODE -> "网络错误"
            NETWORK_ERROR_CODE_TIME_OUT -> "网络请求超时"
            NETWORK_ERROR_CODE_UNKNOWN_HOST -> "未知主机"
            NETWORK_ERROR_CODE_UNKNOWN_SOCKET -> "未知Socket"
            NETWORK_ERROR_CODE_UNKNOWN_SSL -> "未知SSL"
            else -> "未知错误"
        }
    }
}