package com.yola.networklib

import android.text.TextUtils

/**
 *  code 0 表示成功，其他表示失败
 *  data 返回结果
 *  msg  错误信息
 *
 */
open class BaseNetResult<T>(
    var code: Int = 0,
    var msg: String? = null,
    var data: T? = null,
) {

    fun isSuccess(): Boolean {
        return code == 0
    }

}





