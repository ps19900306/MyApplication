package com.nwq.opencv.db.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CoordinateUtils
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule
import org.opencv.core.Mat


@Entity(tableName = "find_target_hsv")
data class FindTargetRgbEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    val tag: String,

    //进行生成时候选的区域
    val targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(PointHSVRuleConverters::class)
    val prList: List<PointRule>,

    //点识别使用时候又几个容错
    var errorTolerance: Int = 0,
) : IFindTarget {

    //上一次找到成功时候
    @Ignore
    private var lastOffsetX: Int = 0
    @Ignore
    private var lastOffsetY: Int = 0

    private val originArea by lazy {
        CoordinateUtils.calculateBoundingRectangle(prList.map { it.point })
    }

    override suspend fun findTarget(): CoordinateArea? {
        val bitmap = imgTake.getLastImg() ?: return null
        return findTargetBitmap(bitmap)
    }

    override fun release() {

    }


    //这里返回的区域是基于整个图标的
    fun findTargetBitmap(bitmap: Bitmap): CoordinateArea? {
        return if (checkImgTask(bitmap)) {
            if (findArea == null) {
                CoordinateArea(lastOffsetX, lastOffsetY, originArea.width, originArea.height)
            } else {
                CoordinateArea(
                    lastOffsetX + findArea!!.x,
                    lastOffsetY + findArea!!.y,
                    originArea.width,
                    originArea.height
                )
            }
        } else {
            return null
        }
    }


    protected fun checkImgTask(
        bitmap: Bitmap,
    ): Boolean {
        if (prList.isEmpty()) {
            return false
        }
        val areaToCheck = findArea?.let { it } ?: CoordinateArea(0, 0, bitmap.width, bitmap.height)
        for (i in areaToCheck.x until areaToCheck.width) {
            for (j in areaToCheck.y until areaToCheck.height) {
                var nowErrorCount = 0
                prList.forEach lit@{
                    if (!it.checkIpr(bitmap, i, j)) {
                        nowErrorCount++
                        if (nowErrorCount > errorTolerance) {
                            return@lit  // 从 lambda 表达式中返回
                        }
                    }
                }
                if (nowErrorCount <= errorTolerance) {
                    lastOffsetX = i
                    lastOffsetY = j
                    return true
                }
            }
        }
        return false
    }
}

