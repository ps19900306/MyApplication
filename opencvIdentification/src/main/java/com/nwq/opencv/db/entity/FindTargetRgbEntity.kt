package com.nwq.opencv.db.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea

import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.converters.PointRuleConverters
import com.nwq.opencv.rgb.PointRule


@Entity(tableName = "find_target_rgb")
data class FindTargetRgbEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(PointRuleConverters::class)
    var prList: List<PointRule>,

    //点识别使用时候又几个容错
    var errorTolerance: Int = 0,
    //上一次找到成功时候
    var lastOffsetX: Int = 0,
    var lastOffsetY: Int = 0,
) : IFindTarget {

    override suspend fun findTarget(): CoordinateArea? {
        var bitmap = imgTake.getLastImg() ?: return null
        return findTargetBitmap(bitmap)
    }

    override fun release() {

    }

    override suspend fun checkVerifyResult(target: CoordinateArea): TargetVerifyResult {
        TODO("Not yet implemented")
    }


    //这里返回的区域是基于整个图标的
    fun findTargetBitmap(bitmap: Bitmap): CoordinateArea? {
        return if (checkImgTask(bitmap)) {
            if (findArea == null) {
                CoordinateArea(
                    lastOffsetX,
                    lastOffsetY,
                    targetOriginalArea.width,
                    targetOriginalArea.height
                )
            } else {
                CoordinateArea(
                    lastOffsetX + findArea!!.x,
                    lastOffsetY + findArea!!.y,
                    targetOriginalArea.width,
                    targetOriginalArea.height
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
        var areaToCheck = findArea?.let { it } ?: CoordinateArea(0, 0, bitmap.width, bitmap.height)
        for (i in areaToCheck.x until areaToCheck.width) {
            for (j in areaToCheck.y until areaToCheck.height) {
                var nowErrorCount = 0
                prList.forEach lit@{
                    if (!it.checkIpr(bitmap, i - targetOriginalArea.x, j - targetOriginalArea.y)) {
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

