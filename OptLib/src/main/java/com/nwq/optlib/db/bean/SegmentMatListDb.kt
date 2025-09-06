package com.nwq.optlib.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nwq.baseutils.MatUtils
import com.nwq.optlib.bean.SegmentMatInfo
import org.opencv.core.Mat

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


    fun performOperations(srcMat: Mat): List<SegmentMatInfo> {
        var areaList = MatUtils.segmentImageByConnectedRegions(
            srcMat,
            minW,
            maxW,
            minH,
            maxH
        )
        if (spacingWidth > 0 || spacingHeight > 0) {
            areaList = MatUtils.mergeRegions(areaList, spacingWidth, spacingHeight)
        }
        val resultList = areaList.map {
            val nowMat = MatUtils.cropMat(srcMat, it)
            val bitmap = MatUtils.grayMatToBitmap(nowMat)
            val info = SegmentMatInfo()
            info.mMat = nowMat
            info.mBitmap = bitmap
            info.coordinateArea = it
            info
        }
        return resultList
    }

}