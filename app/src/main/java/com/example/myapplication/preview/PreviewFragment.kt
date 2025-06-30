package com.example.myapplication.preview

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.graphics.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.base.NavigationContainerActivity2
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentPreviewBinding
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.base.BaseFragment
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.loguitls.L
import com.nwq.simplelist.TextAdapter
import com.nwq.simplelist.TextWarp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PreviewFragment : BaseToolBar2Fragment<FragmentPreviewBinding>() {

    private val viewModel: PreviewViewModel by viewModels({ requireActivity() })
    private lateinit var mTextAdapter: TextAdapter<PreviewOptItem>
    private val TAG = "PreviewFragment"

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
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
                viewModel.mBitmap.collectLatest {
                    binding.imageView.setImageBitmap(it)
                }
            }
        }
        viewModel.initBitMap()

        lifecycleScope.launch {
            binding.previewCoordinateView.nowPoint.collectLatest {
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


    private fun selectPicture() {
        L.i(TAG, "selectPicture")
        PictureSelector.create(requireActivity()).openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    L.i(TAG, "onResult")
                    result?.getOrNull(0)?.let {
                        val opts = BitmapFactory.Options()
                        opts.outConfig = Bitmap.Config.ARGB_8888
                        opts.inMutable = true
                        BitmapFactory.decodeFile(it.realPath, opts)?.let {
                            viewModel.mBitmap.tryEmit(it)
                        }
                        viewModel.path = it.realPath
                        viewModel.type = MatUtils.REAL_PATH_TYPE
                    }
                }

                override fun onCancel() {
                    L.i(TAG, "onCancel")
                }
            })
    }


    private fun onOpt(data: PreviewOptItem) {
        when (data.type) {
            TouchOptModel.FULL_SCREEN -> {
                val ac = requireActivity()
               if (ac is NavigationContainerActivity2) {
                    ac.fullScreen()
                }
            }

            TouchOptModel.SELECT_PICTURE -> {
                selectPicture()
//                lifecycleScope.launch {
//                    val path = touchOptModel.selectPictureFirst(requireActivity())
//                    Log.i(TAG, "onOpt: $path")
//                    path?.let {
//                        FileUtils.readBitmapFromRealPath(path)?.let { bitmap ->
//                            viewModel.path = path
//                            viewModel.type = MatUtils.REAL_PATH_TYPE
//                            viewModel.mBitmap.tryEmit(bitmap)
//                        }
//                    }

            }

            TouchOptModel.RECT_AREA_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible  = false
                    data.coordinate = binding.previewCoordinateView.getRectArea()
                    updatePreviewList()
                    binding.recyclerView.isVisible  = true
                }
            }

            TouchOptModel.CIRCLE_AREA_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible  = false
                    data.coordinate = binding.previewCoordinateView.getCircleArea()
                    updatePreviewList()
                    binding.recyclerView.isVisible  = true
                }
            }

            TouchOptModel.SINGLE_CLICK_TYPE -> {
                lifecycleScope.launch {
                    binding.draggableTextView.isVisible = true
                    binding.recyclerView.isVisible  = false
                    data.coordinate = binding.previewCoordinateView.getPoint()
                    updatePreviewList()
                    binding.draggableTextView.isVisible = false
                    binding.recyclerView.isVisible  = true
                }
            }

            TouchOptModel.MEASURE_DISTANCE_TYPE -> {
                lifecycleScope.launch {
                    binding.recyclerView.isVisible  = false
                    data.coordinate = binding.previewCoordinateView.measureDistance()
                    updatePreviewList()
                    binding.recyclerView.isVisible  = true
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