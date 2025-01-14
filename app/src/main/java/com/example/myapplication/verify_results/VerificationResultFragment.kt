package com.example.myapplication.verify_results


import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.VerifyResultPointAdapter
import com.example.myapplication.databinding.FragmentVerificationResultBinding
import com.nwq.base.BaseFragment
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.FileUtils
import com.nwq.opencv.db.entity.TargetVerifyResult


class VerificationResultFragment(
    private val result: TargetVerifyResult,
    private val mViewModel: VerifyResultPViewModel
) : BaseFragment<FragmentVerificationResultBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerificationResultBinding {
        return FragmentVerificationResultBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        binding.nameTv.text = result.ImgName
        binding.checkBox.isChecked = result.isEffective
        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.setIsEffectiveForId(result.id, isChecked)
        }
        binding.resultTv.text = if (result.hasFind) "hasFind" else "notFind"

        if (!TextUtils.isEmpty(result.ImgName)) {
            binding.srcImg.setImageBitmap(FileUtils.readBitmapFromRootImg(result.ImgName))
        }

        binding.hideTv.setOnClickListener {
            binding.group.visibility = View.GONE
        }

        binding.previewView.clearArea()
        binding.previewView.clearPoint()
        result.resultArea?.let { binding.previewView.addArea(it) }
        result.poinitInfo?.forEach {
            binding.previewView.addDot(CoordinatePoint(it.x, it.y))
        }

        result.poinitInfo?.let {
            binding.pointRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.pointRecyclerView.adapter = VerifyResultPointAdapter(it)
        }


    }

    override fun onResume() {
        super.onResume()
        binding.group.visibility = View.VISIBLE
    }

}