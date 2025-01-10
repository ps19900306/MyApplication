package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.FindTargetType
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.data.PointVerifyResult
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

    //上一次找到成功时候
    var lastOffsetX: Int = 0,
    var lastOffsetY: Int = 0
) : IFindTarget {


    override suspend fun findTarget(): CoordinateArea? {
        if (prList.isEmpty()) {
            return null
        }
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }

    override fun release() {

    }

    /**
     * 检查验证结果的 suspend 函数
     * 该函数用于检查当前图像与目标模板之间的匹配程度
     * 它通过比较图像中每个可能的位置来寻找最佳匹配结果
     *
     * @return 如果找到匹配的结果，则返回 TargetVerifyResult 对象，否则返回 null
     */
    override suspend fun checkVerifyResult(): TargetVerifyResult? {
        // 计算错误容忍度，每5个点允许有一个错误
        val errorT = prList.size / 5
        // 初始化最后一个验证结果为 null
        var last: TargetVerifyResult? = null
        // 获取处理后的 HSV Mat 对象，如果获取失败则直接返回 null
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        // 遍历图像的每一列
        for (i in 0 until srcMat.cols() - targetOriginalArea.x) {
            // 遍历图像的每一行
            for (j in 0 until srcMat.rows() - targetOriginalArea.y) {
                // 初始化当前错误计数
                val nowResult = checkVerifyResult(srcMat, i, j, errorT)
                // 如果当前结果通过验证，则直接返回该结果
                if (nowResult?.hasFind == true) {
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
                hasFind = false,
                type = FindTargetType.HSV,
            )
        }
        return last
    }


    private suspend fun checkVerifyResult(
        srcMat: Mat,
        offsetX: Int,
        offsetY: Int,
        errorT: Int
    ): TargetVerifyResult? {
        // 初始化当前错误计数
        var nowErrorCount = 0
        var passCount = 0
        val list = mutableListOf<PointVerifyResult>()

        // 遍历每个检查规则
        prList.forEach {
            val pointVerifyResult = it.checkBIpr(
                srcMat,
                offsetX - targetOriginalArea.x,
                offsetY - targetOriginalArea.y,
                findArea?.x ?: 0,
                findArea?.y ?: 0
            ) ?: return null
            list.add(pointVerifyResult)
            if (pointVerifyResult.isPass) {
                passCount++
            } else {
                nowErrorCount++
            }
            if (nowErrorCount > errorT) {
                return null
            }
        }
        return TargetVerifyResult(
            tag = keyTag,
            hasFind = nowErrorCount <= errorTolerance,
            type = FindTargetType.HSV,
            poinitInfo = list,
            resultArea = getCoordinateArea(offsetX, offsetY),
            passCount = passCount,
            failCount = nowErrorCount,
            totalCount = prList.size
        )
    }


    //这里返回的区域是基于整个图标的  找到的位置
    private fun findTargetBitmap(mat: Mat): CoordinateArea? {
        return if (checkImgTask(mat)) {
            getCoordinateArea(lastOffsetX, lastOffsetY)
        } else {
            null
        }
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
        for (i in 0 until srcMat.cols() - targetOriginalArea.x) {
            // 遍历图像的每一行
            for (j in 0 until srcMat.rows() - targetOriginalArea.y) {
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

