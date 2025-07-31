package com.nwq.baseutils

import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import com.nwq.baseobj.CoordinateArea
import com.nwq.checkhsv.CheckHSVSame
import com.nwq.checkhsv.CheckHSVSame3
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.features2d.Feature2D
import org.opencv.features2d.SIFT
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

object MatUtils {

    const val STORAGE_ASSET_TYPE = 0
    const val STORAGE_EXTERNAL_TYPE = 1
    const val REAL_PATH_TYPE = 3
    const val TAG = "MatUtils"

    /**
     * 读取并转换HSV色彩空间的Mat对象
     *
     * 根据指定的类型和文件名，从资产目录或外部存储中读取图像，并将其转换为HSV色彩空间的Mat对象
     *
     * @param type 图像存储类型，决定从哪里读取图像（资产目录或外部存储）
     * @param fileName 图像文件名
     * @return 成功读取并转换后返回Mat对象，否则返回null
     */
    fun readHsvMat(type: Int, fileName: String): Mat? {
        // 根据存储类型处理不同的图像读取逻辑
        when (type) {
            STORAGE_ASSET_TYPE -> {
                // 从资产目录读取位图，如果失败则返回null
                val bitmap = FileUtils.readBitmapFromAsset(fileName) ?: return null
                // 将位图转换为HSV色彩空间的Mat对象
                return bitmapToHsvMat(bitmap)
            }

            STORAGE_EXTERNAL_TYPE -> {
                // 从外部存储读取位图，如果失败则返回null
                val bitmap = FileUtils.readBitmapFromRootImg(fileName) ?: return null
                // 将位图转换为HSV色彩空间的Mat对象
                return bitmapToHsvMat(bitmap)
            }

            else -> {
                // 对于不支持的存储类型，返回null
                return null
            }
        }
    }

    fun matToByteArray(mat: Mat): ByteArray {
        val size = (mat.total() * mat.elemSize()).toInt()
        val byteBuffer = ByteBuffer.allocate(size)
        mat.get(0, 0, ByteArray(size).also { byteBuffer.put(it) })
        return byteBuffer.array()
    }

    fun byteArrayToMat(bytes: ByteArray, type: Int, rows: Int, cols: Int): Mat {
        val mat = Mat(rows, cols, type)
        mat.put(0, 0, bytes)
        return mat
    }


    //如果一个色彩空间HSV 根据传入的MaxH MaxS MaxV MinH MinS MinV 来进行过滤，获得MaskMat
    /**
     * 根据传入的HSV最大最小值进行过滤，获得MaskMat
     * @param hsvMat 输入的HSV色彩空间图像
     * @param minH 最小色调值
     * @param maxH 最大色调值
     * @param minS 最小饱和度值
     * @param maxS 最大饱和度值
     * @param minV 最小亮度值
     * @param maxV 最大亮度值
     * @return 过滤后的掩码图像
     */
    fun getFilterMaskMat(
        hsvMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Mat {
        Log.d("getMaskMat", "minH:$minH maxH:$maxH minS:$minS maxS:$maxS minV:$minV maxV:$maxV")
        val lowerBound = Scalar(minH.toDouble(), minS.toDouble(), minV.toDouble())
        val upperBound = Scalar(maxH.toDouble(), maxS.toDouble(), maxV.toDouble())
        val maskMat = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, maskMat)
//        类型：maskMat 是单通道（1通道）的二值图像
//        数据类型：8位无符号整型（CV_8U）
//        值范围：像素值只有两种，0（黑色）或255（白色）
        return maskMat
    }


    //根据连同性能进行区域提取 这样理由后面生成数据字典
    fun findConnectedRegions(destMat: Mat): List<Rect> {
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()

        // 查找轮廓（连通区域）
        Imgproc.findContours(
            destMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL, // 只检测外部轮廓（不包含内孔）
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        // 提取每个轮廓的边界矩形
        val regions = mutableListOf<Rect>()
        for (contour in contours) {
            val rect = Imgproc.boundingRect(contour)
            regions.add(rect)
        }

        return regions
    }


    /**
     * 合并两张掩码图进行逻辑or运算
     *
     * @param maskMat1 第一张掩码图矩阵
     * @param maskMat2 第二张掩码图矩阵
     * @return 运算结果的新掩码图矩阵
     */
    fun mergeMaskMat(maskMat1: Mat, maskMat2: Mat): Mat {
        // 创建输出矩阵
        val resultMat = Mat()
        // 执行逻辑AND运算
        Core.bitwise_or(maskMat1, maskMat2, resultMat)
        return resultMat
    }


    fun createBitmapFromMask(
        maskMat: Mat,
        color: Int = Color.RED,
        backColor: Int = Color.TRANSPARENT
    ): Bitmap {
        // 验证输入掩码类型
        require(maskMat.type() == CvType.CV_8UC1) {
            "掩码必须是单通道8位无符号整型(CV_8UC1)"
        }

        // 创建ARGB_8888格式的Bitmap
        val width = maskMat.cols()
        val height = maskMat.rows()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 获取掩码数据
        val maskData = ByteArray(width * height)
        maskMat.get(0, 0, maskData)

        // 设置Bitmap像素
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                pixels[index] = if (maskData[index].toInt() == 1) color else backColor
            }
        }

        // 将像素数组设置到Bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    }

    /**
     * 根据指定的HSV范围过滤图像
     * 此函数创建一个掩码，该掩码会根据给定的HSV最小值和最大值过滤源图像，然后将过滤后的图像与原始图像进行位与操作，
     * 以提取符合指定HSV范围的图像部分
     *
     * @param srcMat 输入的源图像，预期为HSV色彩空间的Mat对象
     * @param minH 最小色调值
     * @param maxH 最大色调值
     * @param minS 最小饱和度值
     * @param maxS 最大饱和度值
     * @param minV 最小明度值
     * @param maxV 最大明度值
     * @return 返回过滤后的图像，仅包含符合指定HSV范围的部分
     */
    fun filterByHsv(
        srcMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Mat {
        Log.i(TAG, "filterByHsv h $minH:$maxH s $minS:$maxS v $minV:$maxV")
        // 获取基于指定HSV范围的掩码Mat对象
        val maskMat = getFilterMaskMat(srcMat, minH, maxH, minS, maxS, minV, maxV)

        // 返回过滤后的图像
        return filterByMask(srcMat, maskMat)
    }

    /**
     * 使用掩码对源图像进行过滤，提取符合颜色空间的图像部分
     *
     * @param srcMat 源图像Mat对象，需要进行掩码过滤的输入图像
     * @param maskMat 单通道掩码Mat对象，用于指定需要保留的图像区域
     * @return 过滤后的Mat对象，只包含源图像中被掩码指定的区域
     */
    fun filterByMask(srcMat: Mat, maskMat: Mat): Mat {
        // 将单通道掩码转换为三通道，使其与源图像兼容
        val maskMat3Channel = Mat()
        Imgproc.cvtColor(maskMat, maskMat3Channel, Imgproc.COLOR_GRAY2BGR)

        // 执行按位与操作，应用掩码过滤源图像
        val destMat = Mat()
        Core.bitwise_and(srcMat, maskMat3Channel, destMat)
        return destMat
    }

    /**
     * 根据掩码生成新的目标掩码，规则为：原掩码值为0的区域保留，为1的区域不保留
     *
     * @param maskMat 单通道掩码Mat对象，输入掩码
     * @return 生成的新掩码Mat对象，符合过滤规则
     */
    fun generateInverseMask(srcMat: Mat, maskMat: Mat): Mat {
        // 创建全白Mat作为基准
        val whiteMat = Mat(maskMat.size(), maskMat.type(), Scalar(255.0))

        // 对原掩码取反
        val invertedMask = Mat()
        Core.bitwise_not(maskMat, invertedMask)
        return filterByMask(srcMat, invertedMask)
    }


    // 获取点通过特征点
    public fun getPointByImageKeyPoint(hsvMat: Mat, mask: Mat? = null): List<Point> {
        // 创建一个用于存储 BGR 图像的 Mat 对象
        val bgrMat = Mat()
        // 创建一个用于存储灰度图的 Mat 对象
        val grayMat = Mat()
        // 先将 HSV 图像转换为 BGR 图像
        Imgproc.cvtColor(hsvMat, bgrMat, Imgproc.COLOR_HSV2BGR)
        // 再将 BGR 图像转换为灰度图
        Imgproc.cvtColor(bgrMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        val mask2 = if (mask == null) {
            Mat()
        } else {
            mask
        }
        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        val feature2D: Feature2D = SIFT.create()
        // 5. 检测关键点并提取描述符
        feature2D.detectAndCompute(grayMat, mask2, keypoints, descriptors)

        val keypointList = keypoints.toArray().toList()
        return keypointList.map { it.pt }
    }

    //通过范围获取坐标列表
    public fun getPointByRange(
        hsvMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int,
        digits: Int = 1, // 腐蚀运算核大小
        distance: Int = 3 // 边框取点的最小距离
    ): List<Point> {
        // 根据阈值进行二值化
        val lowerBound = Scalar(minH.toDouble(), minS.toDouble(), minV.toDouble())
        val upperBound = Scalar(maxH.toDouble(), maxS.toDouble(), maxV.toDouble())
        val binaryMat = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, binaryMat)

        // 第一次腐蚀计算去除最外层
        val erodedMat1 = Mat()
        val kernel1 = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT,
            Size(digits.toDouble(), digits.toDouble())
        )
        Imgproc.erode(binaryMat, erodedMat1, kernel1)

        // 第二次腐蚀（使用更大的核）
        val erodedMat2 = Mat()
        val kernel2 = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT,
            Size((digits * 2).toDouble(), (digits * 2).toDouble())
        )
        Imgproc.erode(erodedMat1, erodedMat2, kernel2)

        // 做减法得到边框Mat
        val borderMat = Mat()
        Core.subtract(erodedMat1, erodedMat2, borderMat)

        // 收集所有白色像素点
        val points = mutableListOf<Point>()
        for (y in 0 until borderMat.rows()) {
            for (x in 0 until borderMat.cols()) {
                if (borderMat.get(y, x)[0] > 0.0) {
                    points.add(Point(x.toDouble(), y.toDouble()))
                }
            }
        }

        // 按照最小距离过滤点
        return filterPointsByDistance(points, distance)
    }

    /**
     * 根据最小距离过滤点集，确保结果中的每两个点之间的距离都大于等于指定值
     *
     * @param points 原始点集合，包含需要过滤的坐标点
     * @param minDistance 最小允许的距离(像素值)，必须大于0才有意义
     * @return 过滤后的点集合，其中任意两点间的距离都大于等于minDistance
     */
    private fun filterPointsByDistance(points: List<Point>, minDistance: Int): List<Point> {
        if (points.isEmpty() || minDistance <= 0) return points

        // 初始化结果集，总是包含第一个点
        val filteredPoints = mutableListOf<Point>()
        filteredPoints.add(points[0])

        // 遍历剩余所有点
        for (i in 1 until points.size) {
            val current = points[i]
            var isFarEnough = true

            // 检查当前点与已选点之间的距离
            for (existing in filteredPoints) {
                val dx = current.x - existing.x
                val dy = current.y - existing.y
                if (dx * dx + dy * dy < minDistance * minDistance) {
                    isFarEnough = false
                    break
                }
            }

            // 如果满足距离要求则加入结果集
            if (isFarEnough) {
                filteredPoints.add(current)
            }
        }

        return filteredPoints
    }


    /**
     * 判断点是否在指定区域范围内
     *
     * 该函数用于检查一个点是否位于一个由左、右、上、下边界定义的矩形区域之内
     * 点的位置由其坐标（x, y）确定，而矩形区域则由其四周边界的坐标定义
     * 此外，函数还考虑了一个距离因素，用于扩展或收缩矩形区域的边界
     * 这对于在一定容差范围内判断点是否接近矩形区域边缘很有用
     *
     * @param left 矩形区域的左边界
     * @param right 矩形区域的右边界
     * @param top 矩形区域的上边界
     * @param bottom 矩形区域的下边界
     * @param point 待检测的点
     * @param distance 扩展或收缩矩形区域边界的距离
     * @return 如果点在扩展后的矩形区域内，则返回true；否则返回false
     */
    fun isPointWithinDistance(
        left: Int,
        right: Int,
        top: Int,
        bottom: Int,
        point: Point,
        distance: Int
    ): Boolean {
        // 判断点是否在扩展后的矩形区域内
        val extendedLeft = left + distance
        val extendedRight = right - distance
        val extendedTop = top + distance
        val extendedBottom = bottom - distance
        return point.x.toInt() in extendedLeft..extendedRight && point.y.toInt() in extendedTop..extendedBottom
    }


    fun bitmapToMat(bitmap: Bitmap, coordinateArea: CoordinateArea? = null): Mat {
        // 创建一个 Mat 对象
        var mat = Mat()
        // 使用 OpenCV 的 Utils 类将 Bitmap 转换为 Mat
        Utils.bitmapToMat(bitmap, mat)
        // 如果 Bitmap 是 ARGB_8888 格式，需要将其转换为 RGB 格式 去掉A通道
        if (bitmap.config == Bitmap.Config.ARGB_8888) {
            val rgbMat = Mat()
            Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2RGB)
            mat = rgbMat
        }
        return if (coordinateArea != null) {
            cropMat(mat, coordinateArea)
        } else {
            mat
        }
    }


    fun bitmapToHsvMat(bitmap: Bitmap, coordinateArea: CoordinateArea? = null): Mat {
        // 将 Bitmap 转换为 Mat
        val mat = bitmapToMat(bitmap)
        // 创建一个 HSV 格式的 Mat 对象
        val hsvMat = Mat(mat.size(), CvType.CV_8UC3)
        // 将 RGB 格式的 Mat 转换为 HSV 格式的 Mat
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)
        return if (coordinateArea != null) {
            cropMat(hsvMat, coordinateArea)
        } else {
            hsvMat
        }
    }


    fun matToBitmap(srcMat: Mat): Bitmap {
        // 创建一个临时的 Mat 对象，用于存储 ARGB_8888 格式的图像
        val argbMat = Mat()
        // 将 RGB 格式的 Mat 转换为 ARGB_8888 格式
        Imgproc.cvtColor(srcMat, argbMat, Imgproc.COLOR_RGB2RGBA)

        // 创建一个 Bitmap 对象
        val bitmap = Bitmap.createBitmap(argbMat.cols(), argbMat.rows(), Bitmap.Config.ARGB_8888)
        // 使用 OpenCV 的 Utils 类将 Mat 转换为 Bitmap
        Utils.matToBitmap(argbMat, bitmap)
        return bitmap
    }


    fun hsvMatToBitmap(srcMat: Mat): Bitmap {
        // 创建一个临时的 Mat 对象，用于存储 RGB 格式的图像
        val rgbMat = Mat()
        // 将 HSV 格式的 Mat 转换为 RGB 格式的 Mat
        Imgproc.cvtColor(srcMat, rgbMat, Imgproc.COLOR_HSV2RGB)

        // 将 RGB 格式的 Mat 转换为 Bitmap
        return matToBitmap(rgbMat)
    }


    fun cropMat(srcMat: Mat, coordinateArea: CoordinateArea): Mat {
        val dstMat = Mat(coordinateArea.height, coordinateArea.width, srcMat.type())
        srcMat.submat(
            coordinateArea.y,
            coordinateArea.y + coordinateArea.height,
            coordinateArea.x,
            coordinateArea.x + coordinateArea.width
        ).copyTo(dstMat)
        return dstMat
    }


    //chatgpt 提取共同的点
    fun findExactHSVMatch(
        imagePaths: List<String>,
        area: CoordinateArea? = null,
        checkHSVSame: CheckHSVSame = CheckHSVSame3()
    ): Mat? {
        if (imagePaths.isEmpty()) {
            println("没有提供图像路径！")
            return null
        }

        // 加载第一张图像并转换为 HSV
        var baseHSV = getMatFormPaths(imagePaths[0], area) ?: return null

        // 初始化结果矩阵（全黑）
        val mask = Mat(baseHSV.size(), CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))

        // 遍历其他图像
        for (i in 1 until imagePaths.size) {
            // 转换为 HSV 格式
            val nextHSV = getMatFormPaths(imagePaths[i], area) ?: return null
            // 创建临时掩码矩阵
            val tempMask = Mat(baseHSV.size(), CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))


            // 比较 HSV 通道值
            for (row in 0 until baseHSV.rows()) {
                for (col in 0 until baseHSV.cols()) {
                    val basePixel = baseHSV.get(row, col)
                    val nextPixel = nextHSV.get(row, col)

                    if (checkHSVSame.checkHSVSame(
                            basePixel[0],
                            basePixel[1],
                            basePixel[2],
                            nextPixel[0],
                            nextPixel[1],
                            nextPixel[2]
                        )
                    ) {
                        Log.i(TAG, "添加点, $row  $col")
                        tempMask.put(row, col, *basePixel)
                    }
                }
            }

            // 更新主掩码
            Core.bitwise_and(baseHSV, tempMask, mask)
        }

        // 保存结果
        println("完全匹配的 HSV ")
        return mask

    }


    /**
     * 根据图像路径和可选的坐标区域，加载并处理图像，返回HSV色彩空间的图像矩阵
     *
     * @param imagePath 图像文件的路径
     * @param area 可选参数，定义图像的坐标区域，用于裁剪图像
     * @return 返回HSV色彩空间的图像矩阵，如果图像路径为空或处理失败则返回null
     */
    fun getMatFormPaths(imagePath: String, area: CoordinateArea? = null): Mat? {
        Log.i("MatUtils", "getMatFormPaths: $imagePath")
        // 检查图像路径是否为空，如果为空则直接返回null
        if (TextUtils.isEmpty(imagePath)) {
            return null
        }
        // 读取图像并将其存储在Mat对象中
        val image = Imgcodecs.imread(imagePath)
        // 根据是否提供了坐标区域参数，决定是否对图像进行裁剪
        val nextImage = if (area != null) {
            cropMat(image, area)
        } else {
            image
        }
        // 创建一个Mat对象用于存储转换后的HSV图像
        val nextHSV = Mat()
        // 将图像从BGR色彩空间转换为HSV色彩空间
        Imgproc.cvtColor(nextImage, nextHSV, Imgproc.COLOR_BGR2HSV)
        // 返回转换后的HSV图像矩阵
        return nextHSV;
    }


    //chatgpt 通义提供
    fun extractCommonHSVPoints(imagePaths: List<String>, area: CoordinateArea? = null): Mat? {
        if (imagePaths.isEmpty()) {
            println("没有提供图像路径！")
            return null
        }

        // 加载所有图片并转换为 HSV 格式
        val hsvImages = mutableListOf<Mat>()
        for (path in imagePaths) {
            var image = Imgcodecs.imread(path)
            if (image.empty()) {
                println("无法加载图像: $path")
                return null
            }
            area?.let {
                image = cropMat(image, it)
            }

            val hsvImage = Mat()
            Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV)
            hsvImages.add(hsvImage)
        }

        // 确保所有图像的尺寸一致
        val baseSize = hsvImages[0].size()
        if (hsvImages.any { it.size() != baseSize }) {
            println("所有图像的尺寸必须一致！")
            return null
        }

        // 初始化结果图像（黑色背景）
        val result = Mat(baseSize, hsvImages[0].type(), Scalar(0.0, 0.0, 0.0))

        // 遍历每个像素点
        for (row in 0 until hsvImages[0].rows()) {
            for (col in 0 until hsvImages[0].cols()) {
                // 获取第一个图像的像素值作为参考
                val referencePixel = hsvImages[0].get(row, col)

                // 检查所有图像的同一像素是否相同
                var isCommon = true
                for (i in 1 until hsvImages.size) {
                    val currentPixel = hsvImages[i].get(row, col)
                    if (!(referencePixel contentEquals currentPixel)) {
                        isCommon = false
                        break
                    }
                }

                // 如果完全相同，写入结果图像
                if (isCommon) {
                    result.put(row, col, *referencePixel)
                }
            }
        }

        // 保存结果图像
        return result
    }


    /**
     * 计算并返回一组图像的平均背景图像
     * 此函数通过将所有输入图像的像素值相加，然后除以图像数量来计算平均背景
     * 这种方法适用于视频监控等场景，其中背景是静态的，而前景对象是移动的
     * 通过计算平均背景，可以用于背景减除技术，以检测场景中的运动对象
     *
     * @param images 一个包含多个OpenCV Mat对象的列表，代表一系列图像
     * @return 返回一个Mat对象，代表计算出的平均背景图像
     */
    fun calculateBackground(images: List<Mat>): Mat {
        // 初始化背景图像，大小和类型与第一张图像相同，初始值为黑色（所有通道值为0）
        val background = Mat(images[0].size(), images[0].type(), Scalar(0.0))

        // 遍历所有图像，将它们的像素值累加到背景图像中
        for (image in images) {
            Core.add(background, image, background)
        }
        // 将累加后的背景图像除以图像数量，得到平均背景图像
        Core.divide(background, Scalar(images.size.toDouble()), background)

        // 返回计算出的平均背景图像
        return background
    }

    /**
     * 获取指定点的HSV颜色
     */
    fun getHsv(hsvMat: Mat, x: Int, y: Int): DoubleArray? {
        val array = hsvMat.get(y, x)
        return array
    }


    /**
     * 获取指定区域HSV颜色空间的最小和最大值
     *
     * @param hsvMat 输入的HSV颜色空间矩阵
     * @param x 区域左上角的x坐标
     * @param y 区域左上角的y坐标
     * @param width 区域的宽度
     * @param height 区域的高度
     * @return 包含HSV各通道最小和最大值的DoubleArray，格式为[minH, maxH, minS, maxS, minV, maxV]。
     *         如果输入参数无效或区域内无数据，则返回null
     */
    fun getHsv(hsvMat: Mat, x: Int, y: Int, width: Int, height: Int): DoubleArray? {
        // 检查输入参数是否有效
        if (x < 0 || y < 0 || width <= 0 || height <= 0 ||
            x + width > hsvMat.cols() || y + height > hsvMat.rows()
        ) {
            return null
        }

        // 存储所有像素点的HSV值
        val hsvValues = mutableListOf<DoubleArray>()
        // 获取指定区域的子矩阵
        val roi = hsvMat.submat(y, y + height, x, x + width)

        // 遍历子矩阵中的所有像素点
        for (i in 0 until roi.rows()) {
            for (j in 0 until roi.cols()) {
                val pixel = roi.get(i, j)
                hsvValues.add(doubleArrayOf(pixel[0], pixel[1], pixel[2]))
            }
        }

        // 如果没有有效数据则返回null
        if (hsvValues.isEmpty()) return null

        // 计算各HSV通道的最小和最大值
        val minH = hsvValues.minByOrNull { it[0] }?.get(0) ?: 0.0
        val maxH = hsvValues.maxByOrNull { it[0] }?.get(0) ?: 0.0
        val minS = hsvValues.minByOrNull { it[1] }?.get(1) ?: 0.0
        val maxS = hsvValues.maxByOrNull { it[1] }?.get(1) ?: 0.0
        val minV = hsvValues.minByOrNull { it[2] }?.get(2) ?: 0.0
        val maxV = hsvValues.maxByOrNull { it[2] }?.get(2) ?: 0.0

        return doubleArrayOf(minH, maxH, minS, maxS, minV, maxV)
    }

    fun countNonZero(maskMat: Mat): Int {
        return Core.countNonZero(maskMat)
    }


}