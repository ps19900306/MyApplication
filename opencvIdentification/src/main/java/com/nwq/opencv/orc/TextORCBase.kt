package com.nwq.opencv.orc

import android.graphics.Bitmap
import android.graphics.Point
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.CoordinateUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


abstract class TextORCBase(private val targetStr: String) {


    //如果需要裁剪进覆盖实现
    fun getCropArea(): CoordinateArea? {
        return null
    }

    //如果需要裁处理覆盖实现
    fun pretreatment(bitmap: Bitmap): Bitmap {
        return bitmap
    }

    suspend fun recognizeTexts(bitmap: Bitmap): List<CoordinateArea>? {
        //先尝试裁剪
        var result: Bitmap = bitmap
        getCropArea()?.let {
            result = Bitmap.createBitmap(
                bitmap,
                it.x,
                it.y,
                it.width,
                it.height
            )
        }
        //对图片进行预处理以加快识别
        result = pretreatment(result);


        return suspendCancellableCoroutine { continuation ->
            val list = mutableListOf<CoordinateArea>()

            val image = InputImage.fromBitmap(result, 0)
            val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            textRecognizer.process(image)
                .addOnSuccessListener(OnSuccessListener { text ->
                    val blocks = text.textBlocks
                    for (block in blocks) {
                        // val blockText = block.text
                        val lines = block.lines
                        for (line in lines) {
                            val words = line.text
                            if (words.contains(targetStr)) {
                                val points: Array<Point>? = line.cornerPoints
                                //根据点坐标计算坐标区域
                                // val coordinateArea CoordinateUtils.calculateBoundingRectangle(points.map { CoordinatePoint(it.x,it.y) }) //如果不行就换这个实现
                                val coordinateArea = CoordinateArea(
                                    points!![0].x,
                                    points[0].y,
                                    points[2].x,
                                    points[2].y
                                )
                                list.add(coordinateArea)
                            }
                        }
                    }
                    continuation.resume(list)  // 如果没有找到符合条件的坐标点
                })
                .addOnFailureListener(OnFailureListener { e ->
                    // 处理失败情况
                    e.printStackTrace()
                    continuation.resume(null)
                })
        }
    }


}