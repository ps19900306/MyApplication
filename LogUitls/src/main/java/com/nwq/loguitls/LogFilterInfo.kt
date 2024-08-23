package com.nwq.loguitls

data class LogFilterInfo(
    var keyStr: String? = null,
    var level: Int = -1,
    var startTime: Long = -1L,
    var endTime: Long = -1L
)