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
    code: Int = 0,
    msg: String? = null,
    data: T? = null,
    val signature: String? = null
) : BaseNetResult<T>(code, msg, data) {


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





