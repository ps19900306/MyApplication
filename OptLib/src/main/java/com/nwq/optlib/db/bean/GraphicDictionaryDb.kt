package com.nwq.optlib.db.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.loguitls.L
import com.nwq.optlib.GraphicDictionaryResult
import org.opencv.core.Mat


//这里只支持灰度二值化的图
@Entity(tableName = "graphic_dictionary")
class GraphicDictionaryDb {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var keyTag: String = ""

    var description: String = ""


    var dictionaryNameList = mutableListOf<String>()

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE

    //二值化的灰度图
    @Ignore
    var dictionaryMatList = mutableListOf<Mat>()




    suspend fun checkMatList(
        matList: List<Mat>,//待匹配的图
        areaList: List<CoordinateArea>//待匹配的图对应的区域
    ): List<GraphicDictionaryResult>? {
        if (dictionaryNameList.isEmpty()) {
            return null
        }
        if (dictionaryMatList.isEmpty()) {
            dictionaryNameList.forEach {
                val mat = MatUtils.getMat(storageType, it, MatUtils.MAT_TYPE_GRAY, "img/${keyTag}")
                if (mat == null) {
                    L.e("GraphicDictionaryDb", "checkMat:${keyTag}/${it} 构建mat Fail")
                    return null
                }
                dictionaryMatList.add(mat)
            }
        }

        val result = mutableListOf<GraphicDictionaryResult>()
        matList.forEachIndexed { index, mat ->

            var maxSimilarity = -1.0
            var bestMatchIndex = -1

            // 调整图像尺寸以匹配模板
            val targetMat = if (mat.size() != dictionaryMatList[0].size()) {
                val resizedMat = Mat()
                org.opencv.imgproc.Imgproc.resize(mat, resizedMat, dictionaryMatList[0].size())
                resizedMat
            } else {
                mat
            }

            // 遍历所有模板图像，找到最相似的一个
            dictionaryMatList.forEachIndexed { dictIndex, dictMat ->
                // 使用模板匹配计算相似度
                try {
                    val similarity = MatUtils.calculateSimilarity(targetMat, dictMat)
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity
                        bestMatchIndex = dictIndex
                    }

                    // 如果需要调整图像尺寸，释放临时创建的Mat
                    if (targetMat != mat) {
                        targetMat.release()
                    }
                } catch (e: Exception) {
                    L.e("GraphicDictionaryDb", "计算相似度时出错: ${e.message}")
                }
            }

            // 如果需要调整图像尺寸，释放临时创建的Mat
            if (targetMat != mat) {
                targetMat.release()
            }

            // 如果找到了相似度高于阈值的匹配项，则添加到结果中
            if (bestMatchIndex != -1 && maxSimilarity >= 0.8) {
                result.add(
                    GraphicDictionaryResult(
                        dictionaryNameList[bestMatchIndex],
                        areaList[index]
                    )
                )
            }
        }
        return result
    }



}