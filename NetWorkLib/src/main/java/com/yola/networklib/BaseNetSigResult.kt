package com.yola.networklib

import android.text.TextUtils
import com.yola.networklib.uitls.KeyGeneratorUtil

/**
 *  code 0 表示成功，其他表示失败
 *  data 返回结果
 *  msg  错误信息
 *  signature 签名信息
 *  默认使用这个区解析
 */
class BaseNetSigResult<T>(
    var code: Int = 0,
    var msg: String? = null,
    var data: T? = null,
    val signature: String? = null
) {

    companion object {
        fun <T> success(data: T): BaseNetSigResult<T> {
            return BaseNetSigResult(0, null, data)
        }

        fun <T> error(code: Int): BaseNetSigResult<T> {
            return BaseNetSigResult(code, null, null)
        }
    }

    fun isSuccessNoNull(): Boolean {
        if (data == null) {
            code = ErrorCode.VERIFY_DATA_NULL
            return false
        }
        if (code == 0) {
            if (verifySignature()) {
                return true
            } else {
                code = ErrorCode.VERIFY_SIGNATURE_FAIL
                return false
            }
        }
        return false
    }

    fun isSuccess(): Boolean {
        if (code == 0) {
            if (verifySignature()) {
                return true
            } else {
                code = ErrorCode.VERIFY_SIGNATURE_FAIL
                return false
            }
        }
        return false
    }


    fun verifySignature(): Boolean {
        //数据为空时候不需要验证
        if (data == null) {
            return true
        }
        if (TextUtils.isEmpty(signature))
            return false
        return KeyGeneratorUtil.verify(data, code, signature!!)
    }


}





