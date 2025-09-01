package com.nwq.optlib.db.bean

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseutils.MatUtils
import com.nwq.optlib.MatResult
import com.nwq.optlib.bean.HSVRule
import com.nwq.optlib.db.converters.HSVRuleConverters
import org.opencv.core.Mat

@Entity(tableName = "hsv_filter_rule")
class HsvFilterRuleDb : MatResult {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""
    var description: String = ""

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(HSVRuleConverters::class)
    @JvmField
    var ruleList: List<HSVRule> = listOf()

    //对符合的范围设置为白色，不设置白色就是黑色
    var isWhite: Boolean = true
    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int> {
        val hsvMat = if (type == MatUtils.MAT_TYPE_HSV) {
            srcMat
        } else if (type == MatUtils.MAT_TYPE_BGR) {
            MatUtils.bgr2Hsv(srcMat)
        } else {
            Log.i("MatResult", "BinarizationByGray::不支持的类型"+type)
            return Pair(null, type)
        }

        var lastMaskMat: Mat? = null
        ruleList.forEach { rule ->
            val maskMat = MatUtils.getFilterMaskMat(
                hsvMat,
                rule.minH,
                rule.maxH,
                rule.minS,
                rule.maxS,
                rule.minV,
                rule.maxV
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
        return Pair(lastMaskMat, MatUtils.MAT_TYPE_THRESHOLD)
    }

    override fun codeString(): String {
        return StringBuilder().apply {
            append("HsvFilterRuleDb().apply { \n")
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