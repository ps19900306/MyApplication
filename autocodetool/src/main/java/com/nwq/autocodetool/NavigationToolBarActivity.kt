package com.nwq.autocodetool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityNavigationContainerBinding
import com.nwq.base.BaseActivity
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.GsonUtils
import com.nwq.baseutils.T
import com.nwq.loguitls.L

/**
 * 和 NavigationContainerActivity  多了Toolbar
 */
class NavigationToolBarActivity : BaseActivity<ActivityNavigationContainerBinding>() {

    companion object {
        const val TAG = "NavigationContainerActivity"

        /**
         * 启动NavigationContainerActivity。
         *
         * 该方法用于启动一个名为NavigationContainerActivity的活动，并传递一个导航ID，以便该活动可以根据ID加载不同的导航文件。
         * 它主要用于在应用程序中实现模块化导航，其中导航逻辑和目标页面被封装在单独的模块或活动中。
         *
         * @param context 上下文，用于启动活动。通常是当前活动或应用程序上下文。
         * @param navigationID 导航ID，表示需要加载的导航文件的唯一标识符。例如，R.navigation.nav_auto_code 或 R.navigation.nav_auto_hsv_rule。
         */
        fun startNavigationContainerActivity(
            context: Context,
            navigationID: Int,
            bundle: Bundle? = null
        ) {
            // 创建一个意图，用于启动NavigationContainerActivity。
            val intent = Intent(context, NavigationToolBarActivity::class.java)
            Log.i(TAG, "startNavigationContainerActivity navigationID: $navigationID")
            Log.i(TAG, "bundle: ${GsonUtils.toJson(bundle)}")
            // 将导航ID作为额外信息添加到意图中，以便NavigationContainerActivity可以接收并使用它来进行导航。
            intent.putExtra("navigationID", navigationID)
            bundle?.let { intent.putExtras(it) }
            // 使用上下文启动活动，开启新的界面。
            context.startActivity(intent)
        }
    }

    /**
     * 全屏的
     */
    private lateinit var controller: WindowInsetsControllerCompat

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val params = window.attributes
        // 设置布局进入刘海区域
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = params
    }

    override fun initData() {
        L.i(TAG, "initData $this")
        binding.toolbar.menu.clear() // 清除旧菜单
        val navigationID = intent.getIntExtra("navigationID", -1)
        val bundle = intent.extras
        if (navigationID == -1) {
            T.show("navigationID为空")
            finish()
            return
        }
        // 2. 获取 NavController 并绑定 BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // 加载对应的导航文件
        val navController = navHostFragment.navController
        navController.setGraph(navigationID, bundle)

        // 3. 监听导航事件，自动更新 Toolbar 标题和返回按钮
        navController.addOnDestinationChangedListener { _, destination, bundle ->
            fullScreen()
            Log.i(TAG, "onDestinationChanged: ${destination.label}")
            binding.toolbar.isVisible = true
            if (bundle == null) {
                binding.toolbar.title = destination.label // 使用 navigation.xml 中定义的 label
                binding.toolbar.subtitle = ""
            } else {
                val title =
                    bundle.getString(getString(com.nwq.baseutils.R.string.big_title_key))
                if (!TextUtils.isEmpty(title)) {
                    binding.toolbar.title = title
                } else {
                    binding.toolbar.title = destination.label
                }
                val subtitle =
                    bundle.getString(getString(com.nwq.baseutils.R.string.small_title_key))
                if (!TextUtils.isEmpty(subtitle)) {
                    binding.toolbar.subtitle = subtitle
                } else if (!TextUtils.isEmpty(binding.toolbar.subtitle)) {
                    binding.toolbar.subtitle = ""
                }
            }
            binding.toolbar.menu.clear() // 清除旧菜单
        }

        setSupportActionBar(binding.toolbar)
        //设置默认标题
        if (bundle != null) {
            val title = bundle.getString(getString(com.nwq.baseutils.R.string.big_title_key))
            if (!TextUtils.isEmpty(title)) {
                binding.toolbar.title = title
            }
            val subtitle =
                bundle.getString(getString(com.nwq.baseutils.R.string.small_title_key))
            if (!TextUtils.isEmpty(subtitle)) {
                binding.toolbar.subtitle = subtitle
            }
        }

        // 4. 处理 Toolbar 返回按钮点击
        binding.toolbar.setNavigationOnClickListener {
            val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            if (currentFragment is BaseToolBar2Fragment<*>) {
                if (!currentFragment.onBackPress()) {
                    // 如果返回 false，再执行默认返回逻辑
                    finish()
                }
            } else {
                finish()
            }
        }
    }


    override fun createBinding(inflater: LayoutInflater): ActivityNavigationContainerBinding {
        return ActivityNavigationContainerBinding.inflate(inflater)
    }

    public fun fullScreen() {
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        binding.toolbar.isVisible = false
    }

    public fun fullScreenHasTool() {
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 状态栏隐藏
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

}