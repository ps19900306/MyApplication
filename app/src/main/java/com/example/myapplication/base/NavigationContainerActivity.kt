package com.example.myapplication.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityNavigationContainerBinding
import com.nwq.baseutils.T

class NavigationContainerActivity : AppActivity<ActivityNavigationContainerBinding>() {

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
        fun startNavigationContainerActivity(context: Context, navigationID: Int,bundle: Bundle?=null){
            // 创建一个意图，用于启动NavigationContainerActivity。
            val intent = Intent(context, NavigationContainerActivity::class.java)

            // 将导航ID作为额外信息添加到意图中，以便NavigationContainerActivity可以接收并使用它来进行导航。
            intent.putExtra("navigationID", navigationID)
            bundle?.let { intent.putExtras(it) }
            // 使用上下文启动活动，开启新的界面。
            context.startActivity(intent)
        }
    }

    override fun initData() {
        val navigationID = intent.getIntExtra("navigationID", -1)
        if (navigationID != -1) {
            // 加载对应的导航文件
            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(navigationID)
        } else {
            // 如果没有传递有效的导航ID，可以处理错误或关闭活动
            T.show("navigationID为空")
            finish()
        }
    }



    override fun createBinding(inflater: LayoutInflater): ActivityNavigationContainerBinding {
        return ActivityNavigationContainerBinding.inflate(inflater)
    }
}