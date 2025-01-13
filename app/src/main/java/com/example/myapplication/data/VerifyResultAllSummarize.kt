package com.example.myapplication.data

data class VerifyResultAllSummarize(
    var isDeprecated: Boolean = false,//是否需要弃用
    var failList: List<VerifyResultPointSummarize>? = null,
    var passList: List<VerifyResultPointSummarize>? = null,
)