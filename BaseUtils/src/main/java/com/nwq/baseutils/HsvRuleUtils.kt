package com.nwq.baseutils

import com.nwq.data.ColorItem

object HsvRuleUtils{

     fun getColorsList(
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): MutableList<ColorItem> {
        val list = mutableListOf<ColorItem>()
        // 生成所有组合
        list.add(ColorItem(floatArrayOf(minH.toFloat(), maxS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), maxS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(minH.toFloat(), minS.toFloat(), maxV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), minS.toFloat(), maxV.toFloat())))

        list.add(ColorItem(floatArrayOf(minH.toFloat(), maxS.toFloat(), minV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), maxS.toFloat(), minV.toFloat())))

        list.add(ColorItem(floatArrayOf(minH.toFloat(), minS.toFloat(), minV.toFloat())))
        list.add(ColorItem(floatArrayOf(maxH.toFloat(), minS.toFloat(), minV.toFloat())))


        return list
    }
}
