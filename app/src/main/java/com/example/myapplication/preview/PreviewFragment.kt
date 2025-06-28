package com.example.myapplication.preview

import android.content.res.Configuration
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.get
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentPreviewBinding
import com.nwq.base.BaseFragment
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.simplelist.TextAdapter
import com.nwq.simplelist.TextWarp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreviewFragment : BaseFragment<FragmentPreviewBinding>() {

    private val viewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val touchOptModel: TouchOptModel by viewModels({ requireActivity() })
    private lateinit var mTextAdapter: TextAdapter<PreviewOptItem>
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewBinding {
        return FragmentPreviewBinding.inflate(inflater, container, false)
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
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
                viewModel.mBitmap.collectLatest {
                    binding.imageView.setImageBitmap(it)
                }
            }
        }
        viewModel.initBitMap()

        lifecycleScope.launch {
            touchOptModel.nowPoint.collectLatest {
                val bitmap = viewModel.mBitmap.value ?: return@collectLatest
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
        }
        binding.previewCoordinateView.updateList(list)
    }

    private fun onOpt(data: PreviewOptItem) {
        when (data.type) {
            TouchOptModel.FULL_SCREEN -> {
                touchOptModel.fullScreen()
            }

            TouchOptModel.SELECT_PICTURE -> {
                lifecycleScope.launch {
                    val path = touchOptModel.selectPictureFirst(this@PreviewFragment)
                    path?.let {
                        FileUtils.readBitmapFromRealPath(path)?.let { bitmap ->
                            viewModel.path = path
                            viewModel.type = MatUtils.REAL_PATH_TYPE
                            viewModel.mBitmap.tryEmit(bitmap)
                        }
                    }
                }
            }

            TouchOptModel.RECT_AREA_TYPE -> {
                lifecycleScope.launch {
                    data.coordinate = touchOptModel.getRectArea()
                    updatePreviewList()
                }
            }

            TouchOptModel.CIRCLE_AREA_TYPE -> {
                lifecycleScope.launch {
                    data.coordinate = touchOptModel.getCircleArea()
                    updatePreviewList()
                }
            }

            TouchOptModel.SINGLE_CLICK_TYPE -> {
                binding.draggableTextView.isVisible = true


                lifecycleScope.launch {
                    data.coordinate = touchOptModel.getPoint()
                    updatePreviewList()
                    binding.draggableTextView.isVisible = false
                }
            }

            TouchOptModel.MEASURE_DISTANCE_TYPE -> {
                lifecycleScope.launch {
                    data.coordinate = touchOptModel.measureDistance()
                    updatePreviewList()
                }
            }

            else -> {
                T.show("不支持的操作")
            }
        }
    }

}