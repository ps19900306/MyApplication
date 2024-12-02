package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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
) {
    @Ignore
    public val list = mutableListOf<IFindTarget>()

}
