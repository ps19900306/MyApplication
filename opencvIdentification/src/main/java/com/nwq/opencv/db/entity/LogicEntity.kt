package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.constant.LogicJudeResult
import com.nwq.opencv.core.ILogicUnit
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.converters.KeyPointConverters
import com.nwq.opencv.db.converters.LongListConverters


//
@Entity(tableName = "logic_unit")
data class LogicEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String = "",  //描述此逻辑单元用来做什么的

    var functionId: Long = 0, //这个逻辑类是被哪个功能类启动的

    var parentLogicId: Long = 0, //根节点时候LogicId为0
    var findTagId: Long = 0, //判断模块的Id
    var clickKeyTag: Long = 0, //点击事件的Id

    @TypeConverters(LongListConverters::class)
    var nextLogicList: List<Long>? = null,

    @TypeConverters(LongListConverters::class)
    var clearLogicList: List<Long>? = null,

    //根据此条件逻辑进入下一个逻辑单元的列表
    var nextFunctionId: Long = 0,

    var priority: Int = 0,//逻辑单元的优先级别,越高会先进行识别 数字越大优先级别越高

    var isClearOther: Boolean = false,//如果是逻辑单元的最后一个节点 则为true 则会认为此逻辑正常结束

    var consecutiveEntries: Int = -1,//连续进入此方法的最大次数  如果是一个正在运行的状态判断则调低优先级 设置为-1，

    var judeOnFindResult: Int = LogicJudeResult.NORMAL,//如果值大于LogicJudeResult.NORMAL 则会再进入判断的时候进行操作
) : ILogicUnit {

//    @Ignore
//    constructor() : this(id = 0) // 显式忽略无参构造函数



    @Ignore
    private var findTargetList: List<IFindTarget>? = null


    fun getTargetList(): List<IFindTarget>? {
        if (findTargetList == null) {
            val list = mutableListOf<IFindTarget>()
            val database = IdentifyDatabase.getDatabase()
            database.findTargetRecordDao().findById(findTagId) ?: let {
                database.findTargetRgbDao().findByKeyTag(it.keyTag)?.let {
                    list.add(it)
                }
                database.findTargetHsvDao().findByKeyTag(it.keyTag)?.let {
                    list.add(it)
                }
                database.findTargetImgDao().findByKeyTag(it.keyTag)?.let {
                    list.add(it)
                }
                database.findTargetMatDao().findByKeyTag(it.keyTag)?.let {
                    list.add(it)
                }
            }
            findTargetList = list
        }
        return findTargetList
    }


    @Ignore
    private var lastCoordinateArea: CoordinateArea? = null

    override suspend fun jude(): Boolean {
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
    override suspend fun onJude(count: Int): Int {
        //如果发现此任务需要触发特定的事件
        if (judeOnFindResult > LogicJudeResult.NORMAL) {
            return LogicJudeResult.ENABLE_SUB_FUNCTIONS
        }
        if (consecutiveEntries < 0 || count in 1..<consecutiveEntries) {
            //TODO 如果需要进行点击则出发点击事件
            return LogicJudeResult.NORMAL;
        }
        return LogicJudeResult.TIME_OUT
    }

    //
    override suspend fun hasChanged(nowLogicUnitList: MutableList<ILogicUnit>) {
        if ((nextLogicList == null || nextLogicList!!.isEmpty()) && (clearLogicList == null || clearLogicList!!.isEmpty()))
            return

        // 如果存在下一个逻辑单元列表，则将其全部添加到当前列表中
        nextLogicList?.forEach { id ->
            IdentifyDatabase.getDatabase().logicDao().findByKeyId(id)?.let {
                nowLogicUnitList.add(it)
            }
        }

        nextLogicList?.forEach { data ->
            nowLogicUnitList.removeIf { it.getKeyId() == data }
        }
        //如果数据有改动  则进行排序
        nowLogicUnitList.sortBy { it.getPrioritySort() }
    }

    override fun getKeyId(): Long {
        return id
    }

    override fun getChildrenFunctionId(): Long {
        return nextFunctionId
    }

    override fun getPrioritySort(): Int {
        return priority
    }
}