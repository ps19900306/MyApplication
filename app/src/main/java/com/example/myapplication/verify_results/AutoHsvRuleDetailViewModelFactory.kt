package com.example.myapplication.verify_results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AutoHsvRuleDetailViewModelFactory(private val tag: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyResultPViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyResultPViewModel(tag) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}