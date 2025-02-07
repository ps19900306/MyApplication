package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.db.converters.HSVRuleConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

@Entity(tableName = "auto_rule_point")
data class AutoRulePointEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String ="",
    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(HSVRuleConverters::class)
    var prList: List<HSVRule> = listOf(),
    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE,
) : IAutoRulePoint {

    override suspend fun autoPoint(hsvMat: Mat): MutableList<Point> {
        val pointList = mutableListOf<Point>()
        prList.forEach {
            val list =
                MatUtils.getCornerPoint(
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

    @Ignore
    private var mSelected = false

    override fun getIsSelected(): Boolean {
        return mSelected
    }

    override fun setIsSelected(isSelected: Boolean) {
        mSelected = isSelected
    }
}