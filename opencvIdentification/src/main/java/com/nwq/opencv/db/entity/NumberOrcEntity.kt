package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
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


    private fun initHasMap() {
        numberList.forEach { path ->
            val mat = MatUtils.readHsvMat(storageType, path)

        }
    }


    private fun getText(): String? {
        return null
    }


    //设别文字
    public suspend fun orc(): String? {


        return null
    }


}