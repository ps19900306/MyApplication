package com.example.myapplication.function


import android.view.MenuItem
import com.example.myapplication.databinding.FragmentFunctionListBinding
import com.nwq.base.BaseToolBarFragment
import com.example.myapplication.R

class FunctionListFragment : BaseToolBarFragment<FragmentFunctionListBinding>() {

    private val viewModel by v
        FunctionViewModel()
    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_function_list
    }

    override fun getTitleRes(): Int {
        return R.string.function_list
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }


    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_add -> {

            }

            R.id.action_delete_select -> {

            }

            R.id.action_delete_all -> {

            }
        }
    }


    private fun add() {
        val dialog = EditFunctionTitleDialog { name, description ->
            viewModel.insert(FunctionEntity(name, description))
            requireActivity().finish()
        }
    }

    override fun onBackPress() {
        requireActivity().finish()
    }

    override fun initData() {
        super.initData()

    }
}