package com.nwq.baseutils

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

@SuppressLint("StaticFieldLeak")
object FileUtils {
    private val context = ContextUtils.getContext()

    /**
     * 保存字符串到指定文件
     */
    fun saveStringToFile(content: String, fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            file.writeText(content)
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * @param data 要保存的数据
     * @param fileName 文件名
     * @return  File?
     */
    fun checkDocumentsFile(fileName: String): File? {
        // 检查外部存储是否可用
        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) {
            return null
        }

        // 获取外部存储目录
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null

        // 创建文件对象
        val file = File(directory, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
        return file;
    }


    /**
     * 向文件追加一行内容
     *
     * @param file 要追加内容的文件
     * @param content 要追加的内容
     * @return 返回 true 如果追加成功，否则返回 false
     */
    fun appendLineToFile(file: File, content: String): Boolean {
        return try {
            // 使用 FileWriter 的 append 参数设置为 true 来追加内容
            with(FileWriter(file, true)) {
                // 写入换行符，确保新内容从下一行开始
                write("\n")
                // 写入内容到文件
                write(content)
                // 关闭 FileWriter，释放资源
                close()
            }
            true
        } catch (e: IOException) {
            false
        }
    }


    /**
     * 在指定文件末尾追加一行文本。
     *
     * @param fileName 要追加内容的文件名。
     * @param content 要追加的文本内容。
     * @return 追加操作是否成功。
     */
    fun appendLineToFile(fileName: String, content: String): Boolean {
        val file = File(context.filesDir, fileName)
        return try {
            // 使用 FileWriter 的 append 参数设置为 true 来追加内容
            with(FileWriter(file, true)) {
                // 写入换行符
                write("\n")
                write(content)
                close()
            }
            true
        } catch (e: IOException) {
            false
        }
    }


    /**
     * 在指定文件末尾追加一行文本。
     *
     * @param fileName 要追加内容的文件名。
     * @param contents 要追加的文本内容。
     * @return 追加操作是否成功。
     */
    fun appendLineToFile(fileName: String, contents: List<String>): Boolean {
        val file = File(context.filesDir, fileName)
        return try {
            // 如果文件不存在，则创建文件
            if (!file.exists()) {
                file.createNewFile()
            }
            // 使用 FileWriter 的 append 参数设置为 true 来追加内容
            with(FileWriter(file, true)) {
                // 写入换行符
                contents.forEach {
                    write("\n")
                    write(it)
                }
                close()
            }
            true
        } catch (e: IOException) {
            false
        }
    }


    /**
     * 从指定文件读取字符串
     */
    fun readStringFromFile(fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            file.readText()
        } catch (e: IOException) {
            null
        }
    }

    /**
     * 创建文件
     */
    fun createFile(fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            file.createNewFile()
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.delete()
    }

    /**
     * 检查文件是否存在
     */
    fun isFileExists(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }
}