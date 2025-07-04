package com.example.myapplication.base

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseobj.ICoordinate
import com.nwq.loguitls.L
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class TouchOptModel : ViewModel() {

    private val TAG = "TouchOptModel"

    companion object {


        const val FULL_SCREEN = Int.MAX_VALUE
        const val SELECT_PICTURE = Int.MAX_VALUE - 1

        //不拦截的事件
        const val NORMAL_TYPE = 0

        //方形区域
        const val RECT_AREA_TYPE = 1

        //圆形区域
        const val CIRCLE_AREA_TYPE = 2

        //单点击
        const val SINGLE_CLICK_TYPE = 3

        //测算距离
        const val MEASURE_DISTANCE_TYPE = 4
    }

    private val _touchType = MutableStateFlow(NORMAL_TYPE)
    val touchType = _touchType as Flow<Int>
    private val _touchCoordinate = MutableStateFlow<ICoordinate?>(null)
    public val nowPoint = MutableStateFlow(CoordinatePoint(0,0))
    fun updateICoordinate(datae: ICoordinate) {
        _touchCoordinate.value = datae
    }

    fun fullScreen() {
        _touchType.value = FULL_SCREEN
    }

    fun resetTouchOptFlag() {
        _touchType.value = NORMAL_TYPE
    }

    suspend fun getPoint(): CoordinatePoint {
        _touchCoordinate.value = null
        _touchType.value = SINGLE_CLICK_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinatePoint
        }.first()
        _touchType.value = NORMAL_TYPE
        return data as CoordinatePoint
    }

    suspend fun getRectArea(): CoordinateArea {
        _touchCoordinate.value = null
        _touchType.value = RECT_AREA_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateArea && !it.isRound
        }.first()
        _touchType.value = NORMAL_TYPE
        return data as CoordinateArea
    }

    suspend fun getCircleArea(): CoordinateArea {
        _touchCoordinate.value = null
        _touchType.value = CIRCLE_AREA_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateArea && it.isRound
        }.first()
        _touchType.value = NORMAL_TYPE
        return data as CoordinateArea
    }

    suspend fun measureDistance(): CoordinateLine {
        _touchCoordinate.value = null
        _touchType.value = MEASURE_DISTANCE_TYPE
        val data = _touchCoordinate.filter {
            it is CoordinateLine
        }.first()
        _touchType.value = NORMAL_TYPE
        return data as CoordinateLine
    }


    suspend fun selectPicture(fragment: Fragment): ArrayList<LocalMedia?>? =
        suspendCancellableCoroutine { continuation ->
            PictureSelector.create(fragment).openSystemGallery(SelectMimeType.ofImage())
                .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>?) {
                        L.i(TAG, "onResult")
                        continuation.resume(result) // 返回结果
                    }

                    override fun onCancel() {
                        L.i(TAG, "onCancel")
                        continuation.resumeWithException(CancellationException("User cancelled image selection"))
                    }
                })
        }

    suspend fun selectPictureFirst(context: Activity): String? =
        suspendCancellableCoroutine { continuation ->
            // 添加协程取消时的清理逻辑
            continuation.invokeOnCancellation {
                L.i(TAG, "Picture selection was cancelled by the coroutine")
            }

            try {
                PictureSelector.create(context)
                    .openSystemGallery(SelectMimeType.ofImage())
                    .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: ArrayList<LocalMedia?>?) {
                            if (continuation.isActive) {
                                L.i(TAG, "Picture selection completed successfully")
                                val path = result?.firstOrNull()?.realPath
                                if (path != null) {
                                    L.d(TAG, "Selected picture path: $path")
                                } else {
                                    L.w(TAG, "No picture was selected or path was null")
                                }
                                continuation.resume(path)
                            }
                        }

                        override fun onCancel() {
                            if (continuation.isActive) {
                                L.i(TAG, "Picture selection was cancelled by user")
                                continuation.resume(null)
                            }
                        }
                    })
            } catch (e: Exception) {
                L.e(TAG, "Error in picture selection $e")
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }


}