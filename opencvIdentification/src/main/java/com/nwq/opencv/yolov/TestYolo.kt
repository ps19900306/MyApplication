package com.nwq.opencv.yolov

import android.content.Context
import java.io.IOException
import java.nio.MappedByteBuffer
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.nio.channels.FileChannel

class TestYolo {



    private fun test(context: Context) {
        try {
            val tfliteModel: MappedByteBuffer = loadMappedFile(context, "yolov8n.tflite")
            val tflite = Interpreter(tfliteModel, Interpreter.Options().apply {
                // 使用 NNAPI 委托加速推理
                setUseNNAPI(true)
            } )


            // 准备输入数据
            val input = Array(1) { FloatArray(224 * 224 * 3) }

            // 准备输出数据
            val output = Array(1) { FloatArray(1000) }

            // 运行模型
            tflite.run(input, output)

            // 处理输出结果
            processOutput(output)

            // 释放资源
            tflite.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadMappedFile(context: Context, fileName: String): MappedByteBuffer {
        return context.assets.openFd(fileName).use { fileDescriptor ->
            val fileChannel = fileDescriptor.createInputStream().channel
            fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
        }
    }

    private fun processOutput(output: Array<FloatArray>): List<Detection> {
        val detections = mutableListOf<Detection>()
        val confidenceThreshold = 0.5f

        // 假设输出数组的结构为 [num_boxes, 6]，其中每一行包含 [x, y, width, height, confidence, class_index]
        for (i in output[0].indices step 6) {
            val x = output[0][i]
            val y = output[0][i + 1]
            val width = output[0][i + 2]
            val height = output[0][i + 3]
            val confidence = output[0][i + 4]
            val classIndex = output[0][i + 5].toInt()

            if (confidence >= confidenceThreshold) {
                detections.add(Detection(x, y, width, height, confidence, classIndex))
            }
        }

        return detections
    }

    data class Detection(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val confidence: Float,
        val classIndex: Int
    )
}