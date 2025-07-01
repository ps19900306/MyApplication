package com.example.myapplication.auto_hsv_rule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentAutoHsvRuleDetailBinding
import com.example.myapplication.preview.PreviewOptItem
import com.example.myapplication.preview.PreviewViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.loguitls.L
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch
import org.opencv.core.Mat


/**
 * [AutoRulePointEntity]
 */
class AutoHsvRuleDetailFragment : BaseToolBar2Fragment<FragmentAutoHsvRuleDetailBinding>() {

    private val TAG = "AutoHsvRuleDetailFragment"
    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val viewModel: AutoHsvRuleDetailViewModel by viewModels({ requireActivity() })
    private lateinit var mCheckTextAdapter: CheckTextAdapter<HSVRule>
    private var mSelectBitmap: Bitmap? = null

    override fun createBinding(inflater: LayoutInflater): FragmentAutoHsvRuleDetailBinding {
        return FragmentAutoHsvRuleDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_auto_hsv_rule
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = true
        when (menuItem.itemId) {
            R.id.action_select_picture -> {
                selectPicture()
            }

            R.id.action_save -> {
                viewModel.save()

            }

            R.id.action_area -> {
                findArea()
            }

            R.id.action_select_area -> { //根据选中的Rule对图片尽心给过滤
                preViewSelectArea()
            }

            R.id.action_add -> {
                val dialog = ModifyHsvDialog(HSVRule(), getSelectBitmap(), object : CallBack<HSVRule> {
                    override fun onCallBack(data: HSVRule) {
                        viewModel.addData(ICheckTextWrap<HSVRule>(data) {
                            it.toString()
                        })
                    }
                })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }

            R.id.action_delete_select -> {
                 viewModel.prList.tryEmit(mCheckTextAdapter.removeSelectAndGet2())
            }

            R.id.action_delete_all -> {
                viewModel.prList.tryEmit(listOf())
                mCheckTextAdapter.upData(listOf())
            }

            else -> {
                flag = false
            }
        }
        return flag
    }

    private fun selectPicture() {
        L.i(TAG, "selectPicture")
        PictureSelector.create(requireActivity()).openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    L.i(TAG, "onResult")
                    result?.getOrNull(0)?.let { localMedia ->
                        val opts = BitmapFactory.Options()
                        opts.outConfig = Bitmap.Config.ARGB_8888
                        opts.inMutable = true
                        BitmapFactory.decodeFile(localMedia.realPath, opts)?.let {
                            preViewModel.mBitmap = it
                        }
                    }
                }

                override fun onCancel() {
                    L.i(TAG, "onCancel")
                }
            })
    }


    override fun onBackPress(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        if (preViewModel.mBitmap == null) {
            return
        }
        //增加一个新选取点颜色
        preViewModel.optList.find { it.key == R.string.select_point_hsv }?.let { item ->
            preViewModel.getSrcMat()?.let { mat ->
                val point = item.coordinate
                if (point != null && point is CoordinatePoint) {
                    MatUtils.getHsv(mat, point.x, point.y)?.let { da ->
                        val pointHSVRule =
                            HSVRule.getSimple(da[0].toInt(), da[1].toInt(), da[2].toInt())
                        viewModel.addData(ICheckTextWrap<HSVRule>(pointHSVRule) {
                            it.toString()
                        })
                        item.coordinate = null
                    }
                }

            }
        }
    }


    override fun initData() {
        super.initData()
        Log.i(TAG, "initData");
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<HSVRule> {
            override fun onCallBack(data: HSVRule) {
                val dialog =
                    ModifyHsvDialog(data, bitmap = getSelectBitmap(), object : CallBack<HSVRule> {
                        override fun onCallBack(data: HSVRule) {
                            mCheckTextAdapter.notifyDataSetChanged()
                        }
                    })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }
        })
        binding.recycler.adapter = mCheckTextAdapter

        viewModel.init(args.autoHsvId)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.prList.collect {
                    mCheckTextAdapter.upData(it)
                }
            }
        }
    }


    private fun getSelectBitmap(): Bitmap? {
        if (mSelectBitmap != null)
            return mSelectBitmap
        if (preViewModel.mBitmap != null) {
            val co = preViewModel.getCoordinate(key = R.string.select_critical_area)
            if (co != null && co is CoordinateArea) {
                //根据区域队Bitmap进行裁剪
                mSelectBitmap = Bitmap.createBitmap(
                    preViewModel.mBitmap!!,
                    co.x,
                    co.y,
                    co.width,
                    co.height
                )
            }
        }
        return mSelectBitmap
    }

    //预览选中区域
    private fun preViewSelectArea() {
        val rules = mCheckTextAdapter.getSelectedItem()
        if (rules.isEmpty()) {
            T.show("请选择规则")
        }

        getSelectBitmap()?.let { bitMap ->
            val mat = MatUtils.bitmapToHsvMat(bitMap)
            var lastMaskMat: Mat? = null
            rules.forEach {
                val rule = it.getT()
                val maskMat = MatUtils.getFilterMaskMat(
                    mat,
                    rule.minH,
                    rule.maxH,
                    rule.minS,
                    rule.maxS,
                    rule.minV,
                    rule.maxV
                )
                if (lastMaskMat == null) {
                    lastMaskMat = maskMat
                } else {
                    lastMaskMat = MatUtils.mergeMaskMat(lastMaskMat!!, maskMat)
                }
            }
            val resultMap = MatUtils.hsvMatToBitmap(MatUtils.filterByMask(mat, lastMaskMat!!))
            binding.srcImg.setImageBitmap(resultMap)
        } ?: let {
            T.show("请选择图片和关键区域")
        }
    }


    //选择图片和关键区域
    private fun findArea() {
        if (preViewModel.optList.isEmpty()){
            preViewModel.optList.add(
                PreviewOptItem(
                    key = R.string.select_critical_area,
                    type = TouchOptModel.RECT_AREA_TYPE,
                    color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red)
                )
            )
            preViewModel.optList.add(
                PreviewOptItem(
                    key = R.string.select_point_hsv,
                    type = TouchOptModel.SINGLE_CLICK_TYPE,
                    color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.black)
                )
            )
        }
        mSelectBitmap = null
        findNavController().navigate(R.id.action_autoHsvRuleDetailFragment_to_nav_opt_preview)
    }

}