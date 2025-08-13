package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.db.converters.HSVRuleConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

@Entity(tableName = "auto_rule_point")
class AutoRulePointEntity() : IAutoRulePoint {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(HSVRuleConverters::class)
    @JvmField
    var prList: List<HSVRule> = listOf()
    var description: String = ""


    //类型 请在这些类型里面选择
    @AutoHsvRuleType
    var type: Int = AutoHsvRuleType.KEY_POINT

    var targetOriginalArea: CoordinateArea? = null
    var path: String? = null
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE


    override suspend fun autoPoint(hsvMat: Mat): MutableList<Point> {
        val pointList = mutableListOf<Point>()
        prList.forEach {
            val list =
                MatUtils.getPointByRange(
                    hsvMat,
                    it.minH,
                    it.maxH,
                    it.minS,
                    it.maxS,
                    it.minV,
                    it.maxV
                )
            pointList.addAll(list)
        }
        return pointList
    }





    override fun getTag(): String {
        return keyTag
    }

    override fun getDescriptionInfo(): String {
        return description
    }

    @Ignore
    private var mSelected = false

    override fun getIsSelected(): Boolean {
        return mSelected
    }

    override fun setIsSelected(isSelected: Boolean) {
        mSelected = isSelected
    }
}