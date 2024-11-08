package com.example.myapplication.opencv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreviewImgActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewImgBinding
    private val viewModel by viewModels<OpenCvOptModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.showBitmapFlow.collectLatest {
                    binding.bgImg.setImageBitmap(it)
                }
            }
        }

    }



    fun selectPicture(view: View) {
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