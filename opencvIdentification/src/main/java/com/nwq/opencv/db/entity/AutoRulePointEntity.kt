package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

@Entity(tableName = "auto_rule_point")
data class AutoRulePointEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String,
    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(PointHSVRuleConverters::class)
    var prList: List<PointHSVRule>,
) : IAutoRulePoint {
    override fun autoPoint(hsvMat: Mat): List<Point> {
        val list = mutableListOf<Point>()

        return list
    }

    override fun getTag(): String {
        return keyTag
    }
}