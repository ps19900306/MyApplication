package com.yola.networklib

object ErrorCode {


    const val VERIFY_SIGNATURE_FAIL = -100000;// "验证签名失败"
    const val VERIFY_DATA_NULL = -200000;// "验证数据为空"
    const val NETWORK_ERROR_CODE = -10001;// "网络错误"
    const val NETWORK_ERROR_CODE_TIME_OUT = -10002;
    const val NETWORK_ERROR_CODE_UNKNOWN_HOST = -10004;
    const val NETWORK_ERROR_CODE_UNKNOWN_SOCKET = -10005;
    const val NETWORK_ERROR_CODE_UNKNOWN_SSL = -10006;
    const val NETWORK_ERROR_CODE_IO = -10007;


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