package com.example.myapplication.opencv

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import com.nwq.opencv.db.entity.ImageDescriptorEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Point
import org.opencv.features2d.Feature2D
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc

class AutoFindRuleModel : ViewModel() {

    private var selectArea: CoordinateArea? = null
    private var findArea: CoordinateArea? = null
    private var srcBitmap: Bitmap? = null //这个是整个图片是未裁剪的
    private var selectMat: Mat? = null   //hsvMat图 是已经裁剪了的
    private var clickArea: CoordinateArea? = null
    private var hSVRuleList: MutableList<HSVRule> = mutableListOf()
    private fun clearHSVRuleList() {
        hSVRuleList.clear()
    }

    fun addHSVRule(x: Int, y: Int) {
        val d = selectMat?.get(y, x) ?: return
        addHSVRule(d[0], d[1], d[2])
    }

    fun addHSVRule(h: Double, s: Double, v: Double) {

    }

    fun addHSVRule(rule: HSVRule) {
        hSVRuleList.add(rule)
    }

    /**
     * 生成不同的规则SvRule 进行取点
     */
    // 获取高亮区域
    private fun getHighSvRule() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 195, 255, 220, 255)
            list.add(rule)
        }
    }

    //获取浅色
    private fun getHighSvRule2() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 135, 195, 220, 255)
            list.add(rule)
        }
    }

    //获取颜色偏黑色
    private fun getHighSvRule3() {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 135, 255, 160, 220)
            list.add(rule)
        }
    }


    /**
     * 构造数据存入数据库
     */
    private var isBuildHsv: Boolean = true
    private var isBuildRgb: Boolean = true
    private var isBuildImg: Boolean = true
    private var isBuildMat: Boolean = true

    private var maskType: Int = MaskUtils.UN_SET_MASK
    private var keyTag: String? = null //描述显示信息
    private var clickKeyTag: String? = null // 未设置则使用keyTag


    fun performAutoFindRule() {
        val mat = selectMat ?: return
        val bitmap = srcBitmap ?: return
        var area = selectArea ?: return
        if (TextUtils.isEmpty(keyTag)) {
            T.show("请先设置描述")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val record = IdentifyDatabase.getDatabase().findTargetRecordDao().findByKeyTag(keyTag!!)
            if (record != null) {
                T.show("已存在该描述")
            } else {
                val pointList = mutableListOf<Point>()
                hSVRuleList.forEach {
                    val list =
                        MatUtils.getCornerPoint(
                            mat,
                            it.minH,
                            it.maxH,
                            it.minS,
                            it.maxS,
                            it.minV,
                            it.maxV
                        )
                    pointList.addAll(list)
                }
                //这里获取到的点坐标是基于mat的
                buildRgbFindTarget(pointList)
                buildHsvFindTarget(pointList)
                buildImgFindTarget()
                buildMatFindTarget()
                T.show("构建成功")
            }
        }
    }


    private fun buildRgbFindTarget(pointList: MutableList<Point>) {
        if (!isBuildRgb) return
        val list = mutableListOf<PointRule>()
        pointList.forEach {
            val rgbInt = srcBitmap!!.getPixel(
                (it.x + selectArea!!.x).toInt(),
                (it.y + selectArea!!.y).toInt()
            )
            val pointRule = PointRule(
                (it.x + selectArea!!.x).toInt(),
                (it.y + selectArea!!.y).toInt(),
                rgbInt.red,
                rgbInt.green,
                rgbInt.blue
            )
            list.add(pointRule)
        }
        val data = FindTargetRgbEntity(
            keyTag = keyTag!!,
            targetOriginalArea = selectArea!!,
            findArea = findArea,
            prList = list,
        )
        IdentifyDatabase.getDatabase().findTargetRgbDao().insert(data)
    }

    private fun buildHsvFindTarget(pointList: MutableList<Point>) {
        if (!isBuildHsv) return
        val list = mutableListOf<PointHSVRule>()
        pointList.forEach {
            val hsvArray = selectMat!!.get(it.y.toInt(), it.x.toInt())
            val pointRule = PointHSVRule(
                (it.x + selectArea!!.x).toInt(),
                (it.y + selectArea!!.y).toInt(),
                hsvArray[0].toInt(),
                hsvArray[1].toInt(),
                hsvArray[2].toInt()
            )
            list.add(pointRule)
        }
        val data = FindTargetHsvEntity(
            keyTag = keyTag!!,
            targetOriginalArea = selectArea!!,
            findArea = findArea,
            prList = list
        )
        IdentifyDatabase.getDatabase().findTargetHsvDao().insert(data)
    }

    private fun buildImgFindTarget() {
        if (!isBuildImg) return
        //保存图片
        val bitmap = MatUtils.hsvMatToBitmap(selectMat!!)
        FileUtils.saveBitmapToRootImg(bitmap, keyTag!!)

        val data = FindTargetImgEntity(
            keyTag = keyTag!!,
            targetOriginalArea = selectArea!!,
            findArea = findArea,
            storageType = MatUtils.STORAGE_EXTERNAL_TYPE,
            maskType = maskType
        )
        IdentifyDatabase.getDatabase().findTargetImgDao().insert(data)
    }

    private fun buildMatFindTarget() {
        if (!isBuildMat) return
        if (!isBuildImg) {
            val bitmap = MatUtils.hsvMatToBitmap(selectMat!!)
            FileUtils.saveBitmapToRootImg(bitmap, keyTag!!)
        }
        //将特征点保存到数据库
        buildImageDescriptorEntity(selectMat!!, MaskUtils.getMaskMat(selectMat, maskType))
        val data = FindTargetMatEntity(
            keyTag = keyTag!!,
            targetOriginalArea = selectArea!!,
            findArea = findArea,
            storageType = MatUtils.STORAGE_EXTERNAL_TYPE,
            maskType = maskType
        )
        IdentifyDatabase.getDatabase().findTargetMatDao().insert(data)
    }


    // 根据传入的数据获取到描述
    private fun buildImageDescriptorEntity(hsvMat: Mat, mask: Mat?) {
        val points = arrayOf(
            Point(0.0, 0.0),
            Point(hsvMat.cols().toDouble(), 0.0),
            Point(hsvMat.cols().toDouble(), hsvMat.rows().toDouble()),
            Point(0.0, hsvMat.rows().toDouble())
        )

        // 2. 将图像转换为灰度图（很多特征提取算法要求灰度图像）
        // 创建一个用于存储 BGR 图像的 Mat 对象
        val bgrMat = Mat()
        // 创建一个用于存储灰度图的 Mat 对象
        val grayMat = Mat()
        // 先将 HSV 图像转换为 BGR 图像
        Imgproc.cvtColor(hsvMat, bgrMat, Imgproc.COLOR_HSV2BGR)
        // 再将 BGR 图像转换为灰度图
        Imgproc.cvtColor(bgrMat, grayMat, Imgproc.COLOR_BGR2GRAY)


        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        val feature2D: Feature2D = SIFT.create()
        // 5. 检测关键点并提取描述符
        feature2D.detectAndCompute(grayMat, mask, keypoints, descriptors)

        val keypointList = keypoints.toArray().toList()
        val imageDescriptorEntity = ImageDescriptorEntity(
            keyTag = keyTag!!,
            descriptors = MatUtils.matToByteArray(descriptors),
            matType = descriptors.type(),
            matRows = descriptors.rows(),
            matCols = descriptors.cols(),
            keyPointList = keypointList,
            pointList = points.toList()
        )
        IdentifyDatabase.getDatabase().imageDescriptorDao().insertDescriptor(imageDescriptorEntity)
    }
}