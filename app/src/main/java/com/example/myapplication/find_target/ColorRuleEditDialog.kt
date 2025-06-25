package com.example.myapplication.find_target


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.nwq.opencv.rgb.ColorRule

class ColorRuleEditDialog(private var colorRule: ColorRule, private val onSave: (ColorRule) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_color_rule_edit, null)

        // 初始化 EditText 控件
        val etMaxRed = view.findViewById<EditText>(R.id.et_max_red)
        val etMinRed = view.findViewById<EditText>(R.id.et_min_red)
        val etMaxGreen = view.findViewById<EditText>(R.id.et_max_green)
        val etMinGreen = view.findViewById<EditText>(R.id.et_min_green)
        val etMaxBlue = view.findViewById<EditText>(R.id.et_max_blue)
        val etMinBlue = view.findViewById<EditText>(R.id.et_min_blue)
        val etRedToGreenMax = view.findViewById<EditText>(R.id.et_red_to_green_max)
        val etRedToGreenMin = view.findViewById<EditText>(R.id.et_red_to_green_min)
        val etRedToBlueMax = view.findViewById<EditText>(R.id.et_red_to_blue_max)
        val etRedToBlueMin = view.findViewById<EditText>(R.id.et_red_to_blue_min)
        val etGreenToBlueMax = view.findViewById<EditText>(R.id.et_green_to_blue_max)
        val etGreenToBlueMin = view.findViewById<EditText>(R.id.et_green_to_blue_min)

        // 设置初始值
        etMaxRed.setText(colorRule.maxRed.toString())
        etMinRed.setText(colorRule.minRed.toString())
        etMaxGreen.setText(colorRule.maxGreen.toString())
        etMinGreen.setText(colorRule.minGreen.toString())
        etMaxBlue.setText(colorRule.maxBlue.toString())
        etMinBlue.setText(colorRule.minBlue.toString())
        etRedToGreenMax.setText(colorRule.redToGreenMax.toString())
        etRedToGreenMin.setText(colorRule.redToGreenMin.toString())
        etRedToBlueMax.setText(colorRule.redToBlueMax.toString())
        etRedToBlueMin.setText(colorRule.redToBlueMin.toString())
        etGreenToBlueMax.setText(colorRule.greenToBlueMax.toString())
        etGreenToBlueMin.setText(colorRule.greenToBlueMin.toString())

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("编辑颜色规则")
            .setPositiveButton("保存") { _, _ ->
                // 更新 ColorRule 对象
                colorRule = ColorRule(
                    maxRed = etMaxRed.text.toString().toInt(),
                    minRed = etMinRed.text.toString().toInt(),
                    maxGreen = etMaxGreen.text.toString().toInt(),
                    minGreen = etMinGreen.text.toString().toInt(),
                    maxBlue = etMaxBlue.text.toString().toInt(),
                    minBlue = etMinBlue.text.toString().toInt(),
                    redToGreenMax = etRedToGreenMax.text.toString().toFloat(),
                    redToGreenMin = etRedToGreenMin.text.toString().toFloat(),
                    redToBlueMax = etRedToBlueMax.text.toString().toFloat(),
                    redToBlueMin = etRedToBlueMin.text.toString().toFloat(),
                    greenToBlueMax = etGreenToBlueMax.text.toString().toFloat(),
                    greenToBlueMin = etGreenToBlueMin.text.toString().toFloat()
                )
                onSave(colorRule)
            }
            .setNegativeButton("取消", null)
            .create()
    }
}
