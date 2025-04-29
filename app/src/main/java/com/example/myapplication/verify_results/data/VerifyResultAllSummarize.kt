package com.example.myapplication.verify_results.data

data class VerifyResultAllSummarize(
    var isDeprecated: Boolean = false,//是否需要弃用
    var failList: List<VerifyResultPointSummarize>? = null,
    var passList: List<VerifyResultPointSummarize>? = null,
    var thresholdList: MutableList<Double> = mutableListOf<Double>(),
)