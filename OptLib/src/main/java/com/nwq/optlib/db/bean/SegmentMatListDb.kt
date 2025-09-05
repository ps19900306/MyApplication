package com.nwq.optlib.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "segment_mat_list")
class SegmentMatListDb {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var keyTag: String = ""

    var description: String = ""

    var minW: Int = 0
    var maxW: Int = 0
    var minH: Int = 0
    var maxH: Int = 0
    var spacingWidth: Int = 0
    var spacingHeight: Int = 0

    var segmentType: Int = 0


}