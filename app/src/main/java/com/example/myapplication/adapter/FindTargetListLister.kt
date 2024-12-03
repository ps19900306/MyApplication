package com.example.myapplication.adapter

import com.nwq.opencv.db.entity.FindTargetRecord

interface FindTargetListLister {

    fun ondDelete(data: FindTargetRecord)

    fun onHsvBtn(data: FindTargetRecord)

    fun onRgbBtn(data: FindTargetRecord)

    fun onImgBtn(data: FindTargetRecord)

    fun onMatBtn(data: FindTargetRecord)
}