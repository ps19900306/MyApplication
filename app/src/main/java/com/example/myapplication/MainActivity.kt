package com.example.myapplication


import android.view.LayoutInflater
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.nwq.base.BaseActivity


class MainActivity : BaseActivity<ActivityMainBinding>() {
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
            navController.navigateUp() // 返回上一级
        }
    }

    override fun createBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }


}