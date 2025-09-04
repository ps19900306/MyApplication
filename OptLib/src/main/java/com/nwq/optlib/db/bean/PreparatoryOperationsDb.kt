package com.nwq.optlib.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseutils.MatUtils
import com.nwq.loguitls.L
import com.nwq.optlib.MatResult
import com.nwq.optlib.db.IdentifyDatabase
import com.nwq.optlib.db.converters.IntegerListConverters
import com.nwq.optlib.db.converters.LongListConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.core.Mat


//前置操作
@Entity(tableName = "preparatory_operations")
class PreparatoryOperationsDb {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""

    var description: String = ""

    @TypeConverters(IntegerListConverters::class)
    var typeList: List<Int> = listOf()

    @TypeConverters(LongListConverters::class)
    var idList: List<Long> = listOf()

    var mList: List<MatResult>? = null


    private suspend fun initList(): List<MatResult>? {
        if (mList != null && mList!!.isNotEmpty()) {
            return mList
        }
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<MatResult>()
            val cropAreaDao = IdentifyDatabase.getDatabase().cropAreaDao()
            val hsvFilterRuleDao = IdentifyDatabase.getDatabase().hsvFilterRuleDao()
            val grayFilterRuleDao = IdentifyDatabase.getDatabase().grayFilterRuleDao()
            typeList.forEachIndexed { index, type ->
                val matResult = when (type) {
                    MatResult.TYPE_CROP_AREA -> {
                        cropAreaDao.findById(idList[index])
                    }

                    MatResult.TYPE_GRAY_FILTER_RULE -> {
                        grayFilterRuleDao.findById(idList[index])
                    }

                    MatResult.TYPE_HSV_FILTER_RULE -> {
                        hsvFilterRuleDao.findById(idList[index])
                    }

                    else -> null
                }
                if (matResult != null) {
                    list.add(matResult)
                } else {
                    L.e(
                        "PreparatoryOperationsDb",
                        "initList: id:${id} tag:$keyTag index $index matResult is null"
                    )
                }
            }
            mList = list
            mList
        }
    }


    public suspend fun performOperations(bgrMat: Mat): Mat? {
        var lastMat = bgrMat
        var lastType = MatUtils.MAT_TYPE_BGR
        val list = initList()
        list?.forEachIndexed { p, m ->
            val (mat, type) = m.performOperations(lastMat, lastType)
            if (p != 0) {
                lastMat.release()
            }
            if (mat == null) {
                L.e(
                    "PreparatoryOperationsDb",
                    "performOperations: id:${id} tag:$keyTag index $p is null"
                )
                return null
            }
            lastMat = mat
            lastType = type
        }
        return lastMat
    }
}