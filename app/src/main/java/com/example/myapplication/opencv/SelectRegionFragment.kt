package com.example.myapplication.opencv


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSelectRegionBinding
import com.nwq.adapter.KeyTextAdapter
import com.nwq.adapter.ResStrKeyText
import com.nwq.base.BaseFragment
import com.nwq.callback.CallBack
import kotlinx.coroutines.launch


class SelectRegionFragment : BaseFragment<FragmentSelectRegionBinding>(), CallBack<Int> {

    private var itemCount = 3
    private val mTouchOptModel by viewModels<TouchOptModel>({ requireActivity() })

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectRegionBinding {
        return FragmentSelectRegionBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        if (itemCount == 1) {
            binding.functionRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        } else {
            binding.functionRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), itemCount)
        }
        binding.functionRecyclerView.adapter = KeyTextAdapter(getList(), this)
    }

    private fun getList(): List<ResStrKeyText> {
        val list = mutableListOf<ResStrKeyText>()
        list.add(ResStrKeyText(R.string.full_screen))
        list.add(ResStrKeyText(R.string.start_screen))
        list.add(ResStrKeyText(R.string.select_picture))
        list.add(ResStrKeyText(R.string.take_img))
        list.add(ResStrKeyText(R.string.select_critical_area))
        list.add(ResStrKeyText(R.string.find_the_image_area))
        return list
    }

    override fun onCallBack(data: Int) {
        when (data) {
            R.string.full_screen -> {
                mTouchOptModel.fullScreen()
            }
            R.string.select_picture -> {
                mTouchOptModel.selectPicture()
            }
            R.string.take_img -> {
             //   mTouchOptModel.fullScreen()
            }
            R.string.select_critical_area -> {
                selectCriticalArea()
            }
            R.string.find_the_image_area -> {
                selectCriticalArea()
            }
        }
    }


    private fun selectCriticalArea(){
        lifecycleScope.launch {
            val rectArea = mTouchOptModel.getRectArea()
            Log.i("selectCriticalArea","rectArea:$rectArea")
        }
    }

    private fun findImageArea(){
        lifecycleScope.launch {
            val rectArea = mTouchOptModel.getRectArea()
            Log.i("findImageArea","rectArea:$rectArea")
        }
    }

}