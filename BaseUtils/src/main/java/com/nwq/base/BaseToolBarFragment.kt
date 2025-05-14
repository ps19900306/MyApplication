package com.nwq.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nwq.baseutils.R

/**
 * A simple [Fragment] subclass.
 * Use the [AppToolBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
abstract class BaseToolBarFragment<VB : ViewBinding>() : Fragment() {



    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected lateinit var toolbar: Toolbar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.fragment_app_tool_bar, container, false) as LinearLayout
        val childView = inflater.inflate(getLayoutId(), rootView, false)
        toolbar = rootView.findViewById(R.id.toolbar)
        rootView.addView(childView)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        rootView.layoutParams = lp
        _binding = DataBindingUtil.bind(childView);
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData() // 初始化视图，设置监听器等
        setupActionBar()
    }


    open fun setupActionBar() {
        toolbar.title = getString(getTitleRes())
        toolbar.setNavigationOnClickListener { onBackPress() }
        if (getMenuRes() != 0) {
            MenuInflater(requireContext()).apply {
                inflate(getMenuRes(), toolbar.menu)
            }
            toolbar.setOnMenuItemClickListener { menuItem ->
                onMenuItemClick(menuItem)
                true
            }
        }
    }



    abstract fun getLayoutId(): Int
    open fun getMenuRes(): Int {
        return 0
    }

    open fun onMenuItemClick(menuItem: MenuItem) {

    }

    abstract fun getTitleRes(): Int

    abstract fun onBackPress()


    // 设置视图，子类可以重写这个方法来初始化视图
    open fun initData() {

    }

    fun <T : View> findViewById(id: Int): T {
        return binding.root.findViewById<T>(id)
    }
}