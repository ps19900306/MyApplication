package com.nwq.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nwq.baseutils.R
import com.nwq.dialog.SimpleTipsDialog

/**
 * A simple [Fragment] subclass.
 * Use the [AppToolBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
abstract class BaseToolBar2Fragment<VB : ViewBinding>() : Fragment() {


    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater)
        return binding.root
    }

    abstract fun createBinding(inflater: LayoutInflater): VB

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        setHasOptionsMenu(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val a = requireActivity();
        Log.i("BaseToolBarFragment", "Activity" + a + "Fragment" + this.tag);
        a.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(getMenuRes(), menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onMenuItemClick(menuItem)
            }
        }, viewLifecycleOwner)
        initView()
        initData() // 初始化视图，设置监听器等
    }

    abstract fun getMenuRes(): Int
    abstract fun onMenuItemClick(menuItem: MenuItem): Boolean


    abstract fun onBackPress(): Boolean


    // 设置视图，子类可以重写这个方法来初始化视图
    open fun initData() {

    }

    // 设置视图，子类可以重写这个方法来初始化视图
    open fun initView() {

    }


    fun <T : View> findViewById(id: Int): T {
        return binding.root.findViewById<T>(id)
    }

    protected fun showTipsDialog(
        titleRes: Int = 0,
        descriptionRes: Int = 0,
        onClick: (Boolean) -> Unit
    ) {
        SimpleTipsDialog(titleRes, descriptionRes, onClick).showDialog(childFragmentManager)
    }


}