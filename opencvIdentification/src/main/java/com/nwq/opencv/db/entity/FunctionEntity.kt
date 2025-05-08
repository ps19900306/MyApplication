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
    var id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //开始时候的功能模块
    var logicIdList: MutableList<Long>,

    var maxNullCount: Int = 20
) : IFunctionUnit {

    @Ignore
    private var mStuckPointDetection: IStuckPointDetection? = null

    @Ignore
    private var logicList: MutableList<ILogicUnit> = mutableListOf()


    @Ignore
    private var nowNullCount = 0


    private suspend fun isPointDetection(): Boolean {
        nowNullCount = mStuckPointDetection?.checkStuckPoint() ?: nowNullCount
        return nowNullCount >= maxNullCount
    }


    override suspend fun startFunction(): Int {
        if (logicList.isEmpty()) {
            Log.i(keyTag, "INIT_LOGIC_LIST")
            val logicDao = IdentifyDatabase.getDatabase().logicDao()
            logicIdList.forEach { logicId ->
                logicDao.findByKeyId(logicId)?.let {
                    logicList.add(it)
                }
            }
            logicList.sortBy { it.getPrioritySort() }
        }

        var result = NORMAL;
        var lastLogicUnit: ILogicUnit? = null
        var nowLogicUnit: ILogicUnit? = null
        var count = 0;
        Log.i(keyTag, "START_FUNCTION::${logicList.size}")
        //写while循环
        while (result < 0 && logicList.isNotEmpty()) {
            nowLogicUnit = logicList.find {
                it.jude()
            }
            if (nowLogicUnit == null) {
                lastLogicUnit?.hasChanged(logicList);
                if (isPointDetection()) {
                    result = STUCK_POINT_END
                }
            } else {
                mStuckPointDetection?.resetCount()
                if (nowLogicUnit != lastLogicUnit) {
                    lastLogicUnit?.hasChanged(logicList);
                    count = 0;
                } else {
                    count++
                    result = nowLogicUnit.onJude(count)
                    if (result == ENABLE_SUB_FUNCTIONS) {//开启子模块
                        result =
                            getChildrenFunctionUnit(nowLogicUnit.getChildrenFunctionId()).startFunction()
                    }
                }
            }
            lastLogicUnit = nowLogicUnit
        }
        Log.i(keyTag, "EXECUTION_RESULT::$result")
        return result
    }

    private fun getChildrenFunctionUnit(functionID: Long): FunctionEntity {
        return IdentifyDatabase.getDatabase().functionDao().findByKeyId(functionID)
    }

}


