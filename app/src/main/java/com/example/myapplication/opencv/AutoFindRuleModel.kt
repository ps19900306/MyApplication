package com.example.myapplication.opencv

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.entity.LocalMedia
import com.nwq.adapter.CheckKeyText
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.auto_point_impl.HighLightAutoPointImpl
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import com.nwq.opencv.db.entity.ImageDescriptorEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
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


    public fun intBaseData(bitmap: Bitmap, area: CoordinateArea, sMat:Mat?=null,){
        srcBitmap=bitmap
        selectArea=area
        selectMat = sMat ?: MatUtils.bitmapToMat(bitmap,area)
    }


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
     *  下面这些谁HSV过滤规则相关的
     */
    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val mAutoRulePointDao by lazy{ IdentifyDatabase.getDatabase().autoRulePointDao() }
    val iAutoRuleDef by lazy {
        val list = mutableListOf<IAutoRulePoint>()
        list.add(HighLightAutoPointImpl())
        list
    }
    val defCheckKeyTextList by lazy {
        iAutoRuleDef.map { t ->
            CheckKeyText(0, t.getTag(), false)
        }
    }

    // 合并查询逻辑
    val resultsFlow: Flow<List<IAutoRulePoint>> = queryFlow.debounce(1000).flatMapLatest { query ->
        if (query.isEmpty()) {
            mAutoRulePointDao.findAll() // 如果输入为空，查询整个表
        } else {
            mAutoRulePointDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
        }
    }.flowOn(Dispatchers.IO)

    fun updateSearchStr(string: String) {
        queryFlow.value = string
    }


    /**
     * 构造数据存入数据库
     */
    private var isBuildHsv: Boolean = true
    private var isBuildRgb: Boolean = true
    private var isBuildImg: Boolean = false
    private var isBuildMat: Boolean = false

    private var maskType: Int = MaskUtils.UN_SET_MASK
    private var keyTag: String? = null //描述显示信息
    private var clickKeyTag: String? = null // 未设置则使用keyTag


    fun performAutoFindRule(iAutoRulePoint:IAutoRulePoint) {
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
                //获取到关键点
                val keyPointList =  iAutoRulePoint.autoPoint(mat)
                //这里获取到的点坐标是基于mat的
                buildRgbFindTarget(keyPointList)
                buildHsvFindTarget(keyPointList)




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
                (it.x + selectArea!!.x).toInt(), (it.y + selectArea!!.y).toInt()
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
            keyTag = keyTag!!, targetOriginalArea = selectArea!!, findArea = findArea, prList = list
        )
        IdentifyDatabase.getDatabase().findTargetHsvDao().insert(data)
    }

    private fun buildImgFindTarget() {
        if (!isBuildImg) return
        //保存图片
        val bitmap = MatUtils.hsvMatToBitmap(selectMat!!)
        FileUtils.saveBitmapToExternalStorageImg(bitmap, keyTag!!)

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
            FileUtils.saveBitmapToExternalStorageImg(bitmap, keyTag!!)
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