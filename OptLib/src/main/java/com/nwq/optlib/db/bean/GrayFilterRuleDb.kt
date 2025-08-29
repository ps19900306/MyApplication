package com.nwq.optlib.db.bean

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseutils.MatUtils
import com.nwq.optlib.MatResult
import com.nwq.optlib.bean.GrayRule
import com.nwq.optlib.db.converters.GrayRuleConverters
import org.opencv.core.Mat

@Entity(tableName = "gray_filter_rule")
class GrayFilterRuleDb : MatResult {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""

    var description: String = ""

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(GrayRuleConverters::class)
    @JvmField
    var ruleList: List<GrayRule> = listOf()

    //对符合的范围设置为白色，不设置白色就是黑色
    var isWhite: Boolean = true


    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int> {
        val grayMat = if (type == MatUtils.MAT_TYPE_GRAY) {
            srcMat
        } else if (type == MatUtils.MAT_TYPE_BGR) {
            MatUtils.bgr2Gray(srcMat)
        } else if (type == MatUtils.MAT_TYPE_HSV) {
            MatUtils.hsv2Gray(srcMat)
        } else {
            Log.i("MatResult", "BinarizationByGray::不支持的类型")
            return Pair(null, type)
        }

        var lastMaskMat: Mat? = null
        ruleList.forEach { rule ->
            val maskMat = MatUtils.thresholdByRange(
                grayMat,
                rule.min,
                rule.max,
            )
            if (lastMaskMat == null) {
                lastMaskMat = maskMat
            } else {
                val tempMat = MatUtils.mergeMaskMat(lastMaskMat, maskMat)
                lastMaskMat.release()
                maskMat.release()
                lastMaskMat = tempMat
            }
        }

        if (!isWhite && lastMaskMat != null) {
            val tempMat = MatUtils.maskMatInverse(lastMaskMat)
            lastMaskMat.release()
            lastMaskMat = tempMat
        }
        return Pair(lastMaskMat, MatUtils.MAT_TYPE_GRAY)
    }

    override fun codeString(): String {
        return StringBuilder().apply {
            append("GrayFilterRuleDb().apply { \n")
            append("isWhite = $isWhite")
            append("ruleList = listOf(")
            ruleList.forEach {
                append("${it.codeString()},")
            }
            append(")")
            append("}")
        }.toString()
    }

}