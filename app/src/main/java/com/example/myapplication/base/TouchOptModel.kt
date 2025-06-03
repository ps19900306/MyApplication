package com.example.myapplication.base

import androidx.lifecycle.ViewModel
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinateLine
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseobj.ICoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class TouchOptModel : ViewModel() {

    companion object {


        const val FULL_SCREEN = Int.MAX_VALUE
        const val SELECT_PICTURE = Int.MAX_VALUE -1

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


     fun updateICoordinate( datae :ICoordinate){
        _touchCoordinate.value = datae
    }

    fun fullScreen(){
        _touchType.value = FULL_SCREEN
    }

   fun selectPicture(){
       _touchType.value = SELECT_PICTURE
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
}