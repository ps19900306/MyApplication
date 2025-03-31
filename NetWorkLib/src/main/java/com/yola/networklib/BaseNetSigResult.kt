package com.yola.networklib

import android.text.TextUtils

/**
 *  code 0 表示成功，其他表示失败
 *  data 返回结果
 *  msg  错误信息
 *  signature 签名信息
 */
class BaseNetSigResult<T>(
    code: Int = 0,
    msg: String? = null,
    data: T? = null,
    val signature: String? = null
) : BaseNetResult<T>(code, msg, data) {


    fun verifySignature(): Boolean {
        if (TextUtils.isEmpty(signature))
            return true
        return true
    }


}





