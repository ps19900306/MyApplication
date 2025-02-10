package com.nwq.baseutils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.nwq.baseobj.CoordinateArea

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@SuppressLint("StaticFieldLeak")
object FileUtils {
    private val context = ContextUtils.getContext()

    private val TAG = "FileUtils"

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
     * 保存bitmap到根目录/img/@fileName.jpg
     *
     * @param bitmap 要保存的Bitmap对象
     * @param fileName 文件名
     * @return 保存是否成功
     */
    fun saveBitmapToRootImg(bitmap: Bitmap, fileName: String): Boolean {
        // 检查外部存储是否可用
        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) {
            return false
        }

        // 获取根目录下的 img 文件夹
        val directory = File(Environment.getExternalStorageDirectory(), "img")
        if (!directory.exists() && !directory.mkdirs()) {
            return false
        }

        // 创建文件对象
        val file = if (fileName.contains(".")) {
            File(directory, fileName)
        } else {
            File(directory, "$fileName.jpg")
        }

        // 确保父目录存在
        if (!file.parentFile.exists() && !file.parentFile.mkdirs()) {
            return false
        }

        // 将Bitmap保存到文件
        return try {
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            // 扫描文件，使其出现在图库中
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("image/jpeg"),
                null
            )

            // 插入到MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/img"
                    )
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                }
            } else {


                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentValues().apply {
                        put(MediaStore.Images.Media.DATA, file.absolutePath)
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    }
                )
            }

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 从根目录/img/读取Bitmap
     *
     * @param fileName 文件名
     * @return 读取的Bitmap对象，如果读取失败则返回null
     */
    fun readBitmapFromRootImg(fileName: String): Bitmap? {
        // 获取根目录下的 img 文件夹
        val directory = File(Environment.getExternalStorageDirectory(), "img")
        if (!directory.exists()) {
            return null
        }

        // 创建文件对象
        val file = if (fileName.contains(".")) {
            File(directory, fileName)
        } else {
            File(directory, "$fileName.jpg")
        }

        // 检查文件是否存在
        if (!file.exists()) {
            return null
        }

        // 读取文件为Bitmap
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从Asset读取Bitmap
     *
     * @param fileName 文件名
     * @return 读取的Bitmap对象，如果读取失败则返回null
     */
    fun readBitmapFromAsset(fileName: String): Bitmap? {
        return try {
            val inputStream = context.assets.open("$fileName.jpg")
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
     * 将Bitmap保存到相册
     *
     * @param srBitmap 要保存的Bitmap对象
     * @param fileNameStr 保存后的文件名，可以包含扩展名
     * @param coordinateArea 可选参数，指定Bitmap的裁剪区域
     * @return 如果保存成功返回true，否则返回false
     */
    fun saveBitmapToGallery(
        srBitmap: Bitmap,
        fileNameStr: String,
        coordinateArea: CoordinateArea? = null
    ): Boolean {
        // 如果文件名不包含扩展名，则默认添加.jpg扩展名
        val fileName = if (fileNameStr.contains(".")) {
            fileNameStr
        } else {
            "$fileNameStr.jpg"
        }

        // 裁剪Bitmap（如果指定了裁剪区域）
        val bitmapToSave = if (coordinateArea != null) {
            Bitmap.createBitmap(
                srBitmap,
                coordinateArea.x,
                coordinateArea.y,
                coordinateArea.width,
                coordinateArea.height
            )
        } else {
            srBitmap
        }

        // 准备ContentValues以插入MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            // 从Android Q开始，需要指定相对路径
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        // 获取ContentResolver以进行后续操作
        val resolver = context.contentResolver
        var outputStream: OutputStream? = null
        var uri: Uri? = null

        try {
            // 插入MediaStore并获取URI
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri == null) {
                return false
            }

            // 打开输出流以写入数据
            outputStream = resolver.openOutputStream(uri)
            if (outputStream == null) {
                return false
            }

            // 将Bitmap压缩为JPEG格式并写入输出流
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            Log.i(TAG, "saveBitmapToGallery 成功")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "saveBitmapToGallery 失败")
            return false
        } finally {
            // 确保输出流被正确关闭
            outputStream?.close()
        }
    }


    /**
     * 从相册中加载位图
     *
     * @param fileNameStr 图片文件名的字符串表示，可以不包含扩展名
     * @return 如果成功找到并加载图片，则返回Bitmap对象；否则返回null
     */
    fun loadBitmapFromGallery(fileNameStr: String): Bitmap? {
        // 获取内容解析器
        val contentResolver: ContentResolver = ContextUtils.getContext().contentResolver

        // 确保文件名包含扩展名
        val fileName = if (fileNameStr.contains(".")) {
            fileNameStr
        } else {
            "$fileNameStr.jpg"
        }

        // 查询相册中的图片
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                // 获取图片ID
                val idColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val imageId = it.getLong(idColumnIndex)

                // 通过 ID 获取图片的 Uri
                val imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId.toString())

                // 加载 Bitmap
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(imageUri)
                    return BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    inputStream?.close()
                }
            }
        }

        return null // 如果未找到文件或读取失败，返回 null
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

    //因为一些算法可能是通过JPG图训练的 所以这里做一下处理
    suspend fun saveBitmapJpgAndRead(bitmap: Bitmap): Bitmap? {
        val file = File(context.filesDir, "screenshot.jpg")
        // 保存图片为 JPG 格式
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        // 读取图片为 Bitmap
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(file.absolutePath)
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