package com.yola.networklib.uitls

import java.security.MessageDigest

object HashUtil {
    private val md by lazy { MessageDigest.getInstance("MD5") }

    fun md5(input: String): String {
        return md.digest(input.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }

}