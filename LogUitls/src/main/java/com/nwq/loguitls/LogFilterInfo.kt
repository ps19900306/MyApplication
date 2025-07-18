package com.nwq.loguitls

data class LogFilterInfo(
    var keyStr: String? = null,
    var level: Int = -1,
    var startTime: Long = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L, //三天前的时间戳
    var endTime: Long = -1L
)