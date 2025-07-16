package com.example.myapplication.preview

import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.graphics.get
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentPreviewBinding
import com.nwq.baseobj.ICoordinate
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.simplelist.TextAdapter
import com.nwq.simplelist.TextWarp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PreviewFragment : AppToolBarFragment<FragmentPreviewBinding>() {

    private val viewModel: PreviewViewModel by viewModels({ requireActivity() })
    private lateinit var mTextAdapter: TextAdapter<PreviewOptItem>
    private val TAG = "PreviewFragment"

    private val hashMap: MutableMap<Int, MutableStateFlow<ICoordinate>> = mutableMapOf()

    override fun createBinding(inflater: LayoutInflater): FragmentPreviewBinding {
        return FragmentPreviewBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return true
    }

    override fun onBackPress(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()
        val list = viewModel.optList
        if (list.isEmpty()) {
            T.show("没有可以操作的")
            return
        }
        val orientation = resources.configuration.orientation
        val itemCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            5 // 横屏设置为5
        } else {
            3 // 竖屏设置为3
        }
        val dataList = list.map {
            TextWarp<PreviewOptItem>(it) {
                getString(it.resStr)
            }
        }.toMutableList()
        val defList = viewModel.defaultList.map {
            TextWarp<PreviewOptItem>(it) {
                getString(it.resStr)
            }
        }
        dataList.addAll(0, defList);

        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), itemCount)
        mTextAdapter = TextAdapter(mCallBack = object : CallBack<PreviewOptItem> {
            override fun onCallBack(data: PreviewOptItem) {
                onOpt(data)
            }
        })
        binding.recyclerView.adapter = mTextAdapter
        mTextAdapter.upData(dataList)
        updatePreviewList()
        binding.imageView.setImageBitmap(viewModel.mBitmap)
        lifecycleScope.launch {
            binding.previewCoordinateView.nowPoint.collectLatest {
                val bitmap = viewModel.mBitmap ?: return@collectLatest
                val color = bitmap[it.x, it.y]
                // opts.outConfig = Bitmap.Config.ARGB_8888
                binding.draggableTextView.setBackgroundColor(color)
                binding.draggableTextView.text = "(${it.x},${it.y})"
            }
        }
    }

    private fun updatePreviewList() {
        val list = viewModel.optList.filter { it.coordinate != null }.map {
            PreviewCoordinateData(it.coordinate!!, it.color, it.paintWith)
        }.toMutableList()
        list.addAll(viewModel.defaultAreaList)
        binding.previewCoordinateView.updateList(list)
    }


    private fun onOpt(data: PreviewOptItem) {
        when (data.type) {
            TouchOptModel.FULL_SCREEN -> {
                fullScreen()
            }

            TouchOptModel.RECT_AREA_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible = false
                    data.coordinate = binding.previewCoordinateView.getRectArea()
                    updatePreviewList()
                    binding.recyclerView.isVisible = true
                }
            }

            TouchOptModel.CIRCLE_AREA_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible = false
                    data.coordinate = binding.previewCoordinateView.getCircleArea()
                    updatePreviewList()
                    binding.recyclerView.isVisible = true
                }
            }

            TouchOptModel.SINGLE_CLICK_TYPE -> {
                lifecycleScope.launch {
                    binding.draggableTextView.isVisible = true
                    binding.recyclerView.isVisible = false
                    data.coordinate = binding.previewCoordinateView.getPoint()
                    updatePreviewList()
                    binding.draggableTextView.isVisible = false
                    binding.recyclerView.isVisible = true
                }
            }

            TouchOptModel.MEASURE_DISTANCE_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible = false
                    data.coordinate = binding.previewCoordinateView.measureDistance()
                    updatePreviewList()
                    binding.recyclerView.isVisible = true
                }
            }

            else -> {
                T.show("不支持的操作")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}