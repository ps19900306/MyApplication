package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.KeyPointConverters
import com.nwq.opencv.db.converters.PointConverters
import org.opencv.core.KeyPoint
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Point

//如果使用特征值找目标 使用特征值则使用这个类存特征值
@Entity(tableName = "image_descriptors")
data class ImageDescriptorEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var keyTag: String,
    //这些是Mat的
    var matCols: Int,
    var matRows: Int,
    var matType: Int,
    var descriptors: ByteArray,
    @TypeConverters(KeyPointConverters::class)
    @JvmField
    var keyPointList: List<KeyPoint>,

    @TypeConverters(PointConverters::class)
    @JvmField
    var pointList: List<Point>,

    //这些是记录的
    var detectionType: String = "",
    var checkNumber: Int = 0,
    var passNumber: Int = 0,
    var errorNumber: Int = 0,
) {



    @Ignore
    private var mMatOfKeyPoint: MatOfKeyPoint? = null

    public fun setMatOfKeyPoint(matOfKeyPoint: MatOfKeyPoint) {
        mMatOfKeyPoint = matOfKeyPoint
    }

    public fun getMatOfKeyPoint(): MatOfKeyPoint {
        if (mMatOfKeyPoint == null) {
            var points = keyPointList.toTypedArray()
            mMatOfKeyPoint = MatOfKeyPoint(*points)
        }
        return mMatOfKeyPoint!!
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageDescriptorEntity
        if (id != other.id) return false
        if (keyTag != other.keyTag) return false
        if (detectionType != other.detectionType) return false
        if (matCols != other.matCols) return false
        if (matRows != other.matRows) return false
        if (!descriptors.contentEquals(other.descriptors)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + keyTag.hashCode()
        result = 31 * result + detectionType.hashCode()
        result = 31 * result + matCols
        result = 31 * result + matRows
        result = 31 * result + descriptors.contentHashCode()
        return result
    }

}
