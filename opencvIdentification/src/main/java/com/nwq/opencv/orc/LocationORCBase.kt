package com.nwq.opencv.orc


import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.StringUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

abstract class LocationORCBase() {


    private val coordinates: MutableList<CoordinatePoint?> = mutableListOf()

    fun addCoordinate(coordinatePoint: CoordinatePoint?) {
        coordinates.add(coordinatePoint)
        if (coordinates.size > 20) {
            coordinates.removeAt(0)  // 移除最旧的记录
        }
    }


    //如果需要裁剪进覆盖实现
    open fun getCropArea(): CoordinateArea? {
        return null
    }

    //如果需要裁处理覆盖实现
    open fun pretreatment(bitmap: Bitmap): Bitmap {
        return bitmap
    }


    suspend fun recognizeText(bitmap: Bitmap): CoordinatePoint? {

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
            val image = InputImage.fromBitmap(result, 0)
            val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            textRecognizer.process(image)
                .addOnSuccessListener(OnSuccessListener { text ->
                    // 在这里处理识别结果
                    var hasReturn = false
                    val blocks = text.textBlocks
                    for (block in blocks) {
                        val blockText = block.text
                        val list = StringUtils.extractNumbers(blockText)
                        if (list.size == 2) {
                            val coordinatePoint = CoordinatePoint(list[0], list[1])
                            if (!hasReturn) {
                                hasReturn = true
                                continuation.resume(coordinatePoint)
                            }
                        }
                    }
                    if (!hasReturn) {
                        continuation.resume(null)  // 如果没有找到符合条件的坐标点
                    }
                })
                .addOnFailureListener(OnFailureListener { e ->
                    // 处理失败情况
                    e.printStackTrace()
                    continuation.resume(null)
                })
        }
    }

}