package com.nwq.baseutils

import com.google.gson.Gson

object GsonUtils {
    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return gson.fromJson(json, clazz)
    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }
    public val gson = Gson()

}