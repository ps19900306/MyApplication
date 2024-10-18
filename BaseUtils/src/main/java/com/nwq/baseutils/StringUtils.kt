package com.nwq.baseutils

object StringUtils {

    fun extractNumbers(text: String): List<Int> {
        val pattern = Regex("\\d+")
        return pattern.findAll(text).map { it.value.toInt() }.toList()
    }


}