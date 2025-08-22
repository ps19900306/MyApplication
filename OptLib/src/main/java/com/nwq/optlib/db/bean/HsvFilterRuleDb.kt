package com.nwq.optlib.db.bean

import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.optlib.bean.HSVRule
import com.nwq.optlib.db.converters.HSVRuleConverters

class HsvFilterRuleDb {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""
    var description: String = ""

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(HSVRuleConverters::class)
    @JvmField
    var prList: List<HSVRule> = listOf()

    //对范围类的验证是否设置未白色，不设置黑色就是白色
    var isWhite: Boolean = true
}