package com.example.myapplication.opencv

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.ActivityPreviewImgBinding
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.base.BaseActivity
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.loguitls.L
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PreviewImgActivity : BaseActivity<ActivityPreviewImgBinding>() {
    private val TAG = PreviewImgActivity::class.java.simpleName +"nwq"
    private val viewModel by viewModels<OpenCvPreviewModel>()
    private val mTouchOptModel by viewModels<TouchOptModel>()
    private lateinit var controller: WindowInsetsControllerCompat

    override fun createBinding(inflater: LayoutInflater): ActivityPreviewImgBinding {
        return ActivityPreviewImgBinding.inflate(layoutInflater)
    }


    override fun beforeSetContentView() {
        super.beforeSetContentView()
        controller = WindowInsetsControllerCompat(window, window.decorView)
        fullScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val params = window.attributes
        // 设置布局进入刘海区域
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = params
    }

    override fun onResume() {
        super.onResume()
        //打印屏幕尺寸大小
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        L.i(TAG, "size: screenWidth：$screenWidth width： $width height： $height")
        fullScreen()
    }

    private fun fullScreen() {
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }


    override fun initData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showBitmapFlow.collectLatest {
                    //打印Bitmap尺寸
                    if (it != null) {
                        val w = it.width
                        val h = it.height
                        L.i(TAG, "size: showBitmapFlow  $w $h")
                    }
                    binding.bgImg.setImageBitmap(it)
                }
            }
        }

        lifecycleScope.launch {
            mTouchOptModel.touchType.collectLatest {
                when (it) {
                    TouchOptModel.NORMAL_TYPE -> {
                        binding.navContain.visibility = View.VISIBLE
                        nowMode = it
                    }

                    TouchOptModel.FULL_SCREEN -> {
                        fullScreen()
                        mTouchOptModel.resetTouchOptFlag()
                    }

                    TouchOptModel.SELECT_PICTURE -> {
                        L.i(TAG, "selectPicture")
                        selectPicture()
                        mTouchOptModel.resetTouchOptFlag()
                    }

                    TouchOptModel.RECT_AREA_TYPE,
                    TouchOptModel.CIRCLE_AREA_TYPE,
                    TouchOptModel.SINGLE_CLICK_TYPE,
                    TouchOptModel.MEASURE_DISTANCE_TYPE,
                    -> {
                        binding.navContain.visibility = View.INVISIBLE
                        nowMode = it
                    }
                }
            }
        }

//        binding.button2.singleClick {
//            SetSHVFilterDialog().show(supportFragmentManager, "SHV");
//        }

        checkPermission()

    }

    override fun getPermission(): Array<String>? {
        return arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private fun selectPicture() {
        L.i(TAG, "selectPicture")
        PictureSelector.create(this).openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    L.i(TAG, "onResult")
                    result?.getOrNull(0)?.let {
                        val opts = BitmapFactory.Options()
                        opts.outConfig = Bitmap.Config.ARGB_8888
                        opts.inMutable = true
                        BitmapFactory.decodeFile(it.realPath, opts)?.let {
                            viewModel.setScrMap(it)
                        }
                    }
                    viewModel.result = result
                }

                override fun onCancel() {
                    L.i(TAG, "onCancel")
                }


            })
    }




    override fun onPermissionPass() {
        L.i(TAG, "onPermissionPass")
    }


    /**
     * 这个是进行点和区域选取的
     */
    private var starX = 1F
    private var starY = 1F
    private var isFirst = true
    private var nowMode = TouchOptModel.NORMAL_TYPE
    private var lastTime = 0L
    private val cancelInterval = 3000L
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //打印位置
        L.i(TAG, "onTouchEvent  ${ev.x}:: $${ev.y}")
        if (nowMode == TouchOptModel.NORMAL_TYPE) {
            return super.onTouchEvent(ev)
        }
        when (nowMode) {
            TouchOptModel.SINGLE_CLICK_TYPE -> {
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()
                    }
                } else {
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        starX = ev.x
                        starY = ev.y
                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            mTouchOptModel.updateICoordinate(CoordinatePoint(starX, starY))
                        }
                        isFirst = true
                    }
                }
            }

            TouchOptModel.RECT_AREA_TYPE, TouchOptModel.CIRCLE_AREA_TYPE -> {
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                        lastTime = System.currentTimeMillis()

                    }

                } else {
                    val isCircle = nowMode == TouchOptModel.CIRCLE_AREA_TYPE
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        val coordinateArea =
                            createCoordinateArea(starX, starY, ev.x, ev.y, isCircle)
                        binding.previewView.setArea(coordinateArea)
                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateArea =
                                createCoordinateArea(starX, starY, ev.x, ev.y, isCircle)
                            binding.previewView.setArea(coordinateArea)
                            mTouchOptModel.updateICoordinate(coordinateArea)
                        } else {
                            binding.previewView.clearArea()
                        }
                        isFirst = true
                    }
                }
            }

            TouchOptModel.CIRCLE_AREA_TYPE -> {
                if (isFirst) {
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        starX = ev.x
                        starY = ev.y
                        isFirst = false
                    }
                } else {
                    if (ev.action == MotionEvent.ACTION_MOVE) {
                        val coordinateLine = CoordinateLine(
                            CoordinatePoint(starX, starY),
                            CoordinatePoint(ev.x, ev.y)
                        )
                        binding.previewView.setLine(coordinateLine)
                    } else if (ev.action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - lastTime < cancelInterval) {
                            val coordinateLine = CoordinateLine(
                                CoordinatePoint(starX, starY),
                                CoordinatePoint(ev.x, ev.y)
                            )
                            binding.previewView.setLine(coordinateLine)
                            mTouchOptModel.updateICoordinate(coordinateLine)
                        } else {
                            binding.previewView.clearLine()
                        }
                        isFirst = true
                    }
                }
            }
        }
        return true
    }

    private fun createCoordinateArea(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        b: Boolean = false
    ): CoordinateArea {
        val sX = if (x1 > x2) x2 else x1
        val sY = if (y1 > y2) y2 else y1
        return CoordinateArea(sX, sY, Math.abs(x1 - x2), Math.abs(y1 - y2), b)
    }
}