package com.nwq.opencv.db.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.opencv.constant.LogicJudeResult.Companion.ENABLE_SUB_FUNCTIONS
import com.nwq.opencv.constant.LogicJudeResult.Companion.NORMAL
import com.nwq.opencv.constant.LogicJudeResult.Companion.STUCK_POINT_END
import com.nwq.opencv.core.IFunctionUnit
import com.nwq.opencv.core.ILogicUnit
import com.nwq.opencv.core.IStuckPointDetection
import com.nwq.opencv.db.IdentifyDatabase


//每一个都是一个可以执行的功能 并不是一个独立的
@Entity(tableName = "function_entity")
data class FunctionEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    var description: String,
//    //开始时候的功能模块
//    var logicIdList: MutableList<Long> = mutableListOf(),

    var maxNullCount: Int = 20
) : IFunctionUnit {

    @Ignore
    private var mStuckPointDetection: IStuckPointDetection? = null

    //这个是当前会进行检测判读的逻辑模块
    @Ignore
    private var nowLogicList: MutableList<ILogicUnit> = mutableListOf()

    //这个是全部的逻辑模块
    @Ignore
    private var allLogicList: MutableList<ILogicUnit> = mutableListOf()

    @Ignore
    private var nowNullCount = 0


    private suspend fun isPointDetection(): Boolean {
        nowNullCount = mStuckPointDetection?.checkStuckPoint() ?: nowNullCount
        return nowNullCount >= maxNullCount
    }


    //
    override suspend fun startFunction(): Int {
        if (allLogicList.isEmpty()) {
            Log.i(keyTag, "INIT_LOGIC_LIST")
            val logicDao = IdentifyDatabase.getDatabase().logicDao()
            logicDao.findByFunctionIdRoot(id).let {
                allLogicList.addAll(it)
            }
            logicDao.findByFunctionId(id).let { list ->
                allLogicList.clear()
                allLogicList.addAll(list)

                nowLogicList.addAll(list.filter { it.parentLogicId == 0L })

            }



            nowLogicList.sortBy { it.getPrioritySort() }
        }

        var result = NORMAL;
        var lastLogicUnit: ILogicUnit? = null
        var nowLogicUnit: ILogicUnit? = null
        var count = 0;
        Log.i(keyTag, "START_FUNCTION::${nowLogicList.size}")
        //写while循环
        while (result < 0 && nowLogicList.isNotEmpty()) {
            nowLogicUnit = nowLogicList.find {
                it.jude()
            }
            if (nowLogicUnit == null) {
                lastLogicUnit?.onHasChanged(nowLogicList, allLogicList);
                if (isPointDetection()) {
                    result = STUCK_POINT_END
                }
            } else {
                mStuckPointDetection?.resetCount()
                if (nowLogicUnit != lastLogicUnit) {
                    lastLogicUnit?.onHasChanged(nowLogicList, allLogicList);
                    count = 0;
                } else {
                    count++
                    result = nowLogicUnit.onJude(count)
                    if (result == ENABLE_SUB_FUNCTIONS) {//开启子模块
                        result =
                            getChildrenFunctionUnit(nowLogicUnit.getChildrenFunctionId())?.startFunction()
                                ?: ENABLE_SUB_FUNCTIONS
                    }
                }
            }
            lastLogicUnit = nowLogicUnit
        }
        Log.i(keyTag, "EXECUTION_RESULT::$result")
        return result
    }


    private fun getChildrenFunctionUnit(functionID: Long): FunctionEntity? {
        return IdentifyDatabase.getDatabase().functionDao().findByKeyId(functionID)
    }

}


