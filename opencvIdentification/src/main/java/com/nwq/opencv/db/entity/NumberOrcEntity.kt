package com.nwq.opencv.db.entity

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.imgtake.ImgTake
import org.opencv.core.Mat

@Entity(tableName = "number_orc")
class NumberOrcEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    //对区域进行二值化
    var binaryRuleId: Long = -1L

    //识别文字的区域
    var findArea: CoordinateArea? = null

    //文字图片的前缀
    var pathNamePrefix: String? = null

    //图片的字库
    var numberList: List<String> = listOf()

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE

    @Ignore
    var numberHasMap: HashMap<Int, Mat> = HashMap()


    //初始化图片
    private fun initHasMap() {
        numberList.forEach { path ->
            val mat = MatUtils.readHsvMat(storageType, path) ?: return@forEach

            try {
                if (pathNamePrefix.isNullOrEmpty()) {
                    val key = path.toIntOrNull() ?: path.hashCode()
                    numberHasMap[key] = mat
                } else if (path.startsWith(pathNamePrefix!!) && path.length > pathNamePrefix!!.length) {
                    val key =
                        path.substring(pathNamePrefix!!.length).toIntOrNull() ?: path.hashCode()
                    numberHasMap[key] = mat
                } else {
                    val key = path.hashCode()
                    numberHasMap[key] = mat
                }
            } catch (e: Exception) {
                // 处理可能的异常，如字符串转换或索引越界
                val key = path.hashCode()
                numberHasMap[key] = mat
            }
        }
    }


    private fun getText(): String? {
        return null
    }


    //设别文字
    public suspend fun orc(): String? {
        val srcMat = ImgTake.imgTake.getHsvMat(findArea) ?: return null

//        类型：destMat 是单通道（1通道）的二值图像
//        数据类型：8位无符号整型（CV_8U）
//        值范围：像素值只有两种，0（黑色）或255（白色）
        val destMat = MatUtils.getPointByRange(srcMat, 0, 255, 0, 255, 0, 255)




        return null
    }


}