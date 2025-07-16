package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nwq.opencv.click.ClickBuilderUtils
import com.nwq.opencv.click.ClickExecuteUtils

@Entity(tableName = "click_area")
data class ClickEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String,
    var x: Int,
    var y: Int,
    var with: Int,
    var height: Int,
    var isRound: Boolean = true,//是否是圆形区域，默认是方形状

    // 点击区域建立时候的 这个是图像的  如果isFixed=false  那么这个值就必须设置
    var findTargetId: Long = 0,
    //如果设置了找图则默认需要修正 不然就是固定
    // var isFixed: Boolean = true, //点击位置是否是固定的 如果非固定的需要 找到图片根据偏差值进行修正

    //如果是多 个点击区域 成排列时候   如果二行四列 通过这个去点击
    var spacingX: Int, //创建点击区域的原始 相对找寻目标坐标的偏移量X
    var spacingY: Int, //创建点击区域的原始 相对找寻目标坐标的偏移量Y
) {


    //执行点击事件
    suspend fun optClick(offsetY: Int = 0, offsetX: Int = 0) {
        val s = ClickBuilderUtils.buildClickGestureDescription(
            x + offsetX,
            y + offsetY,
            with,
            height,
            isRound,
            0
        )
        ClickExecuteUtils.executeClick(s);
    }

    fun toStringSimple(): String {
        return "ClickEntity(id=$id, keyTag='$keyTag', x=$x, y=$y, with=$with, height=$height, isRound=$isRound, findTargetId=$findTargetId)"
    }


}

