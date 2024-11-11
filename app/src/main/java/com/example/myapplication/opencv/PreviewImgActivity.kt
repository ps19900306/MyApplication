package com.example.myapplication.opencv

import BaseActivity
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.databinding.ActivityPreviewImgBinding
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.baseutils.singleClick
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreviewImgActivity : BaseActivity<ActivityPreviewImgBinding>() {
    private val TAG =PreviewImgActivity::class.java.simpleName
    private val viewModel by viewModels<OpenCvOptModel>()
    override fun createBinding(inflater: LayoutInflater): ActivityPreviewImgBinding {
        return ActivityPreviewImgBinding.inflate(layoutInflater)
    }

    override fun initData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showBitmapFlow.collectLatest {
                    Log.i(TAG, "showBitmapFlow collectLatest: $it")
                    binding.bgImg.setImageBitmap(it)
                }
            }
        }
        binding.button.singleClick {
            checkPermission()
        }
        binding.button2.singleClick {
            SetSHVFilterDialog().show(supportFragmentManager, "SHV");
        }
    }

    override fun getPermission(): Array<String>? {
        return arrayOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    }

    override fun onPermissionPass() {
        PictureSelector.create(this).openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    result?.getOrNull(0)?.let {
                        val opts = BitmapFactory.Options()
                        opts.outConfig = Bitmap.Config.ARGB_8888
                        opts.inMutable = true
                        BitmapFactory.decodeFile(it.realPath, opts)?.let {
                            viewModel.setScrMap(it)
                        }
                    }
                }

                override fun onCancel() {}
            })
    }


}