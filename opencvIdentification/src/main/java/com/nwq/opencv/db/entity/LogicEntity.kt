package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.ILogicUnit
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.converters.KeyPointConverters
import com.nwq.opencv.db.converters.LongListConverters


//
@Entity(tableName = "logic_unit")
data class LogicEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var keyTag: String = "",  //描述此逻辑单元用来做什么的
    var findTag: String = "", //判断模块的Tag
    var clickKeyTag: String? = null, //点击事件的Tag
    @TypeConverters(LongListConverters::class)
    var nextList: List<Long>? = null,
    var judeTime: Int = -1,
    var isEnd: Boolean = false,
    var errorCount: Int = 10
) : ILogicUnit {

    @Ignore
    private var findTargetList: List<IFindTarget>? = null

    fun getTargetList(): List<IFindTarget>? {
        if (findTargetList == null) {
            val list = mutableListOf<IFindTarget>()
            val database = IdentifyDatabase.getDatabase()
            database.findTargetRgbDao().findByKeyTag(findTag)?.let {
                list.add(it)
            }
            database.findTargetHsvDao().findByKeyTag(findTag)?.let {
                list.add(it)
            }
            database.findTargetImgDao().findByKeyTag(findTag)?.let {
                list.add(it)
            }
            database.findTargetMatDao().findByKeyTag(findTag)?.let {
                list.add(it)
            }
            findTargetList = list
        }
        return findTargetList
    }


    @Ignore
    private var lastCoordinateArea: CoordinateArea? = null

    override suspend fun jude(): Boolean {
        if (judeTime == 0) {
            return false
        }
        getTargetList()?.forEach {
            val coordinateArea = it.findTarget()
            if (coordinateArea != null) {
                lastCoordinateArea = coordinateArea
                return true
            }
        }
        return false
    }


    //当本次jude()返回为True 时，入本方法  count连续进入次数  Boolean是否进行错误上报
    override suspend fun onJude(nowLogicUnitList: List<ILogicUnit>, count: Int): Boolean {
        if (errorCount in 1..<count) {
            return true;
        }
//        clickArea?.let { area ->
//            if (count % 2 == 1) {
////                ClickBuilderUtils.buildClick(lastCoordinateArea!!, area, 0)?.let {
////                    ClickExecuteUtils.optClickTask(it)
////                }
//            }
//        }
        return false
    }

    //当上一张图jude()返回为True时本张图进进入的不是次方法
    override suspend fun hasChanged(nowLogicUnitList: MutableList<ILogicUnit>) {
        // 如果存在下一个逻辑单元列表，则将其全部添加到当前列表中
        nextList?.forEach { id ->
            IdentifyDatabase.getDatabase().logicDao().findByKeyId(id)?.let {
                nowLogicUnitList.add(it)
            }
        }
        // 如果判断次数大于0，则减少判断次数
        if (judeTime > 0) {
            judeTime--
        }
        // 如果判断次数为0，表明已达到设定的判断次数，从当前列表中移除自身
        if (judeTime == 0) {
            nowLogicUnitList.remove(this)
        }
    }

    override suspend fun isEnd(): Boolean {
        return isEnd
    }

    override suspend fun getTag(): String {
        return keyTag
    }

}