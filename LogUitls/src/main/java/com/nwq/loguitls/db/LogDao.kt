package com.nwq.loguitls.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.loguitls.LogLevel

@Dao
interface LogDao {
    @Insert
    suspend fun insert(log: LogEntity)

    @Insert
    suspend fun insert(log: List<LogEntity>)

    // 删除所有 createTime 小于指定时间的日志记录
    @Query("DELETE FROM logs WHERE recordTime < :timeThreshold")
    fun deleteLogsBefore(timeThreshold: Long): Int


    // 删除所有 createTime 小于指定时间的日志记录
    @Query("SELECT * FROM logs WHERE level >=:level and recordTime>=:startTimeThreshold and recordTime<=:endTimeThreshold and createTime >=:createTimeThreshold")
    fun queryByTime(
        level: Int = LogLevel.VERBOSE,
        startTimeThreshold: Long = -1,
        endTimeThreshold: Long = Long.MAX_VALUE,
        createTimeThreshold: Long = -1,
    ): Int

    // 查询所有 tag 包含特定字符串的日志记录
    @Query("SELECT * FROM logs WHERE tag LIKE '%' || :searchText || '%' and level >=:level and recordTime>=:startTimeThreshold and recordTime<=:endTimeThreshold and createTime >=:createTimeThreshold")
    fun queryByTag(
        searchText: String,
        level: Int = LogLevel.VERBOSE,
        startTimeThreshold: Long = -1,
        endTimeThreshold: Long = Long.MAX_VALUE,
        createTimeThreshold: Long = -1,
    ): List<LogEntity>

}