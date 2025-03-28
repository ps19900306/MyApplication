package com.nwq.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater


import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    private val REQUEST_CODE_PERMISSION = 1001

    protected var hasPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetContentView()
        _binding = createBinding(layoutInflater)
        setContentView(binding.root)
        beforeInitData()
        initData()
    }

    open fun beforeSetContentView() {

    }

    //建议基类 或者统一封装父类使用此方法  子类使用initData
    open fun beforeInitData() {

    }


    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun createBinding(inflater: LayoutInflater): VB

    open fun getPermission(): Array<String>? {
        return null
    }

    protected fun checkPermission() {
        if (hasPermission)
            return
        val permissions = getPermission()
        if (permissions.isNullOrEmpty()) {
            hasPermission = true
            return
        }
        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            Log.i("PreviewImgActivity", "onPermissionPass")
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSION
            )
        } else {
            hasPermission=true
            onPermissionPass()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            var allPermissionsGranted = true

            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                hasPermission=true
                onPermissionPass()
            } else {
                onPermissionFail()
            }
        }
    }

    protected open fun onPermissionPass() {
        // 默认实现，子类可以重写
    }

    protected open fun onPermissionFail() {
        // 默认实现，子类可以重写
    }


}
