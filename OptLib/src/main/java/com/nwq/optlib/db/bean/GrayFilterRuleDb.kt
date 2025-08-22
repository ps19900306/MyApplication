package com.nwq.optlib.db.bean

import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.optlib.bean.GrayRule
import com.nwq.optlib.db.converters.GrayRuleConverters


class GrayFilterRuleDb {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""
    var description: String = ""

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(GrayRuleConverters::class)
    @JvmField
    var prList: List<GrayRule> = listOf()

    //对范围类的验证是否设置未白色，不设置黑色就是白色
    var isWhite: Boolean = true
}