package com.example.myapplication.opencv


import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSelectRegionBinding
import com.luck.picture.lib.utils.ToastUtils
import com.nwq.adapter.CheckKeyText
import com.nwq.adapter.KeyTextAdapter
import com.nwq.adapter.KeyTextCheckAdapter
import com.nwq.adapter.ResStrKeyText
import com.nwq.base.BaseFragment
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.opencv.IAutoRulePoint
import com.nwq.view.SimpleImgFragment
import kotlinx.coroutines.launch


class SelectRegionFragment : BaseFragment<FragmentSelectRegionBinding>(), CallBack<Int> {

    private var itemCount = 3
    private val mTouchOptModel by viewModels<TouchOptModel>({ requireActivity() })
    private val autoFindRuleModel by viewModels<AutoFindRuleModel>({ requireActivity() })
    private val openCvPreviewModel by viewModels<OpenCvPreviewModel>({ requireActivity() })
    private var mKeyTextCheckAdapter: KeyTextCheckAdapter? = null
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectRegionBinding {
        return FragmentSelectRegionBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        val orientation = resources.configuration.orientation
        itemCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            5 // 横屏设置为5
        } else {
            3 // 竖屏设置为3
        }
        if (itemCount == 1) {
            binding.functionRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        } else {
            binding.functionRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), itemCount)
        }
        binding.functionRecyclerView.adapter = KeyTextAdapter(getList(), this)

        /**
         * 下面是更新HSV过滤规则的
         */
        binding.filterEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                autoFindRuleModel.updateSearchStr(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        binding.hsvRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                autoFindRuleModel.resultsFlow.collect {
                    val adapterList = mutableListOf<CheckKeyText>()
                    adapterList.addAll(autoFindRuleModel.defCheckKeyTextList)
                    adapterList.addAll(it.map { t ->
                        CheckKeyText(1, t.getTag(), false)
                    })
                    mKeyTextCheckAdapter =
                        KeyTextCheckAdapter(list = adapterList, isSingle = true)
                    binding.hsvRecyclerView.adapter = mKeyTextCheckAdapter!!
                }
            }
        }
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
                // mTouchOptModel.fullScreen()
            }

            R.string.select_critical_area -> {
                selectCriticalArea()
            }

            R.string.find_the_image_area -> {
                selectCriticalArea()
            }
        }
    }


    private fun selectCriticalArea() {
        if (openCvPreviewModel.srcBitmap == null) {
            T.show("请先选择原始图片")
            return
        }
        lifecycleScope.launch {
            val rectArea = mTouchOptModel.getRectArea()
            Log.i("selectCriticalArea", "rectArea:$rectArea")
            val selectMat = openCvPreviewModel.getSelectMat(rectArea)
            autoFindRuleModel.intBaseData(openCvPreviewModel.srcBitmap!!, rectArea,selectMat)

            selectMat?.let {
                val  selectBitmap= MatUtils.hsvMatToBitmap(selectMat)
                val  dialogFragment = SimpleImgFragment(selectBitmap);
                dialogFragment.show(requireActivity().supportFragmentManager, "SimpleImgFragment")
            }
        }
    }

    private fun findImageArea() {
        lifecycleScope.launch {
            val rectArea = mTouchOptModel.getRectArea()
            Log.i("findImageArea", "rectArea:$rectArea")
        }
    }

}