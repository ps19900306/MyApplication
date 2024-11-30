package com.nwq.opencv.db.entity

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
import org.opencv.core.Mat


@Entity(tableName = "find_target_hsv")
data class FindTargetHsvEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //识别规则 这里的坐标信息是基于全图的
    @TypeConverters(PointHSVRuleConverters::class)
    var prList: List<PointHSVRule>,

    //点识别使用时候又几个容错
    var errorTolerance: Int = 0,
) : IFindTarget {

    //上一次找到成功时候
    @Ignore
    private var lastOffsetX: Int = 0
    @Ignore
    private var lastOffsetY: Int = 0


    override suspend fun findTarget(): CoordinateArea? {
        if (prList.isEmpty()) {
            return null
        }
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }

    override fun release() {

    }


    //这里返回的区域是基于整个图标的  找到的位置
    private fun findTargetBitmap(mat: Mat): CoordinateArea? {
        return if (checkImgTask(mat)) {
            if (findArea == null) {
                CoordinateArea(lastOffsetX, lastOffsetY, targetOriginalArea.width, targetOriginalArea.height)
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


    /**
     * 检查图像任务函数
     * 该函数遍历给定的图像矩阵，寻找与目标图像匹配的区域
     * 它通过应用一系列的检查规则来确定两图像之间的偏移量
     *
     * @param srcMat 源图像矩阵，用于进行匹配检查
     * @return 如果找到匹配的图像区域，则返回true；否则返回false
     */
    private fun checkImgTask(
        srcMat: Mat,
    ): Boolean {
        // 遍历图像的每一列
        for (i in 0 until srcMat.cols()) {
            // 遍历图像的每一行
            for (j in 0 until srcMat.rows()) {
                // 初始化当前错误计数
                var nowErrorCount = 0
                // 遍历每个检查规则
                prList.forEach lit@{
                    // 如果当前规则检查失败，则增加错误计数
                    if (!it.checkIpr(srcMat, i - targetOriginalArea.x, j - targetOriginalArea.y)) {
                        nowErrorCount++
                        // 如果错误超过容忍度，则从当前规则检查中返回
                        if (nowErrorCount > errorTolerance) {
                            return@lit
                        }
                    }
                }
                // 如果错误在容忍范围内，记录偏移量并返回匹配成功
                if (nowErrorCount <= errorTolerance) {
                    lastOffsetX = i
                    lastOffsetY = j
                    return true
                }
            }
        }
        // 如果没有找到匹配的区域，返回失败
        return false
    }
}

