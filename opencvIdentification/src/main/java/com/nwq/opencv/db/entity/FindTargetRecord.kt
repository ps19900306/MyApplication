package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IFindTarget

/**
 * 要进行设置操作，所以这个是
 * 总控的
 */
@Entity(tableName = "find_target_all")
data class FindTargetRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String,
    //描述信息
    var description: String = "",


    var findArea: CoordinateArea? = null,

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea? = null,


    var path: String = "",//源文件 存放  可能是全路径(非asset) 也可能是文件名称（asset文件夹的）
    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE,
) {


}
