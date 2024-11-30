package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.opencv.click.ClickArea
import com.nwq.opencv.db.converters.KeyPointConverters

//
@Entity(tableName = "logic_unit")
class LogicEntity {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0

    var keyTag: String = ""

    var clickKeyTag: String? = null


}