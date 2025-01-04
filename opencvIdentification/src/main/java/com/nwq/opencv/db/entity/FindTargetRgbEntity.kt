package com.nwq.opencv.db.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.FindTargetType

import com.nwq.opencv.IFindTarget
import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.db.converters.PointRuleConverters
import com.nwq.opencv.rgb.PointRule
import org.opencv.core.Mat


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

    override suspend fun checkVerifyResult(): TargetVerifyResult? {
        // 计算错误容忍度，每5个点允许有一个错误
        val errorT = prList.size / 5
        // 初始化最后一个验证结果为 null
        var last: TargetVerifyResult? = null
        // 获取处理后的 HSV Mat 对象，如果获取失败则直接返回 null
        var bitmap = imgTake.getLastImg() ?: return null
        var areaToCheck = findArea?.let { it } ?: CoordinateArea(0, 0, bitmap.width, bitmap.height)
        for (i in areaToCheck.x until areaToCheck.width - targetOriginalArea.x) {
            for (j in areaToCheck.y until areaToCheck.height- targetOriginalArea.y) {
                val nowResult = checkVerifyResult(bitmap, i, j, errorT)
                // 如果当前结果通过验证，则直接返回该结果
                if (nowResult?.isPass == true) {
                    return nowResult
                }
                // 如果当前结果不为空，并且最后一个结果为空或者当前结果的通过计数大于最后一个结果，则更新最后一个结果
                if (nowResult != null && (last == null || nowResult.passCount > last.passCount)) {
                    last = nowResult
                }
            }
        }
        if (last==null){
            last = TargetVerifyResult(
                tag = keyTag,
                isPass = false,
                type = FindTargetType.RGB,
            )
        }
        return last
    }


    /**
     * 检查验证结果的私有协程函数
     * 该函数用于验证目标区域是否符合预期，通过一系列检查规则来确定验证是否通过
     *
     * @param srcMat 原始图像的Mat对象，用于验证过程
     * @param offsetX X轴的偏移量，用于调整验证位置
     * @param offsetY Y轴的偏移量，用于调整验证位置
     * @param errorT 错误容忍度，即允许的最大错误数量
     * @return 如果验证通过则返回TargetVerifyResult对象，否则返回null
     */
    private suspend fun checkVerifyResult(
        bitmap: Bitmap,
        offsetX: Int,
        offsetY: Int,
        errorT: Int
    ): TargetVerifyResult? {
        // 初始化当前错误计数
        var nowErrorCount = 0
        // 初始化通过验证的计数
        var passCount = 0
        // 创建一个列表用于存储每个点的验证结果
        val list = mutableListOf<PointVerifyResult>()

        // 遍历每个检查规则
        prList.forEach {
            // 对每个规则进行验证，如果验证失败则返回null
            val pointVerifyResult = it.checkBIpr(
                bitmap,
                offsetX - targetOriginalArea.x,
                offsetY - targetOriginalArea.y,
                findArea?.x ?: 0,
                findArea?.y ?: 0
            ) ?: return null
            // 将验证结果添加到列表中
            list.add(pointVerifyResult)
            // 根据验证结果更新通过或错误计数
            if (pointVerifyResult.isPass) {
                passCount++
            } else {
                nowErrorCount++
            }
            // 如果错误计数超过容忍度，则返回null
            if (nowErrorCount > errorT) {
                return null
            }
        }
        // 返回验证结果对象，包含验证是否通过、验证类型、点信息、结果区域等
        return TargetVerifyResult(
            tag = keyTag,
            isPass = nowErrorCount <= errorTolerance,
            type = FindTargetType.RGB,
            poinitInfo = list,
            resultArea = getCoordinateArea(offsetX, offsetY),
            passCount = passCount,
            failCount = nowErrorCount,
            totalCount = prList.size
        )
    }

    private fun getCoordinateArea(offsetX: Int, offsetY: Int): CoordinateArea {
        return if (findArea == null) {
            CoordinateArea(
                offsetX,
                offsetY,
                targetOriginalArea.width,
                targetOriginalArea.height
            )
        } else {
            CoordinateArea(
                offsetX + findArea!!.x,
                offsetY + findArea!!.y,
                targetOriginalArea.width,
                targetOriginalArea.height
            )
        }
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
        for (i in areaToCheck.x until areaToCheck.width - targetOriginalArea.x) {
            for (j in areaToCheck.y until areaToCheck.height- targetOriginalArea.y) {
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

