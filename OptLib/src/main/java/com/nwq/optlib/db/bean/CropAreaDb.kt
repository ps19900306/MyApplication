package com.nwq.optlib.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.optlib.MatResult
import org.opencv.core.Mat

@Entity(tableName = "crop_area")
class CropAreaDb() : MatResult {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var coordinateArea: CoordinateArea = CoordinateArea()
    var keyTag: String = ""
    var description: String = ""

    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int> {
        return Pair(MatUtils.cropMat(srcMat, coordinateArea), type)
    }

    override fun codeString(): String {
        return "val cropArea = CropAreaDb() \n cropArea.coordinateArea = ${coordinateArea.codeString()}\n"
    }
}