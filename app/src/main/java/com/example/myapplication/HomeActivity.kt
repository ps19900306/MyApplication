package com.example.myapplication


import android.Manifest
import android.view.LayoutInflater
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityHomeBinding
import com.nwq.base.BaseActivity
import com.nwq.base.BaseToolBar2Fragment

class HomeActivity : BaseActivity<ActivityHomeBinding>() {


    override fun getPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun initData() {
        // 1. 设置 Toolbar 替代 ActionBar

        setSupportActionBar(binding.toolbar)

        // 2. 获取 NavController 并绑定 BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // 3. 监听导航事件，自动更新 Toolbar 标题和返回按钮
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = destination.label // 使用 navigation.xml 中定义的 label
            binding.toolbar.menu.clear() // 清除旧菜单

            // 显示返回按钮（非顶级目标时）
            if (destination.id != R.id.nav_function &&
                destination.id != R.id.nav_target &&
                destination.id != R.id.nav_rule
            ) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        // 4. 处理 Toolbar 返回按钮点击
        binding.toolbar.setNavigationOnClickListener {
            val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            if (currentFragment is BaseToolBar2Fragment<*>) {
                if (!currentFragment.onBackPress()) {
                    // 如果返回 false，再执行默认返回逻辑
                    navController.navigateUp()
                }
            } else {
                navController.navigateUp()
            }
        }

        checkPermission();
    }

    override fun createBinding(inflater: LayoutInflater): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(inflater)
    }
}