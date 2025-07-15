package com.example.myapplication.click

import android.view.LayoutInflater
import android.view.MenuItem
import com.example.myapplication.R
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.databinding.FragmentClickDetailBinding

class ClickDetailFragment : AppToolBarFragment<FragmentClickDetailBinding>() {
    override fun createBinding(inflater: LayoutInflater): FragmentClickDetailBinding {
        return FragmentClickDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_click_detail
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_select_all -> {

            }

            R.id.action_delete_all -> {

            }

            R.id.action_reverse_all -> {

            }
        }
        return true
    }

    override fun onBackPress(): Boolean {
        return false
    }
}