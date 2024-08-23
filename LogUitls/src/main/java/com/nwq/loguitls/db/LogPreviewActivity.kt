package com.nwq.loguitls.db

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nwq.loguitls.LogLevel
import com.nwq.loguitls.R
import com.nwq.loguitls.databinding.ActivityLogPreviewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LogPreviewActivity : AppCompatActivity() {
    private lateinit var viewModel: DbViewModel
    private lateinit var adapter: LogListAdapter
    private lateinit var binding: ActivityLogPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DbViewModel::class.java)

        setupRecyclerView()
        setupFilters()
        observeLogs()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerViewLogs
        val adapter = LogListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFilters() {
        // Spinner for log levels
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.log_levels,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLevel.adapter = spinnerAdapter

        // EditTexts for filtering
        binding.editTextTag.doOnTextChanged { text, _, _, _ ->
            viewModel.updateTag(text?.toString())
        }
        binding.spinnerLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.updateLevel(LogLevel.values()[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.editTextStartTimeThreshold.doOnTextChanged { text, _, _, _ ->
            viewModel.updateStartTimeThreshold(text?.toString()?.toLongOrNull() ?: -1)
        }
        binding.editTextEndTimeThreshold.doOnTextChanged { text, _, _, _ ->
            viewModel.updateEndTimeThreshold(text?.toString()?.toLongOrNull() ?: Long.MAX_VALUE)
        }
        binding.editTextCreateTimeThreshold.doOnTextChanged { text, _, _, _ ->
            viewModel.updateCreateTimeThreshold(text?.toString()?.toLongOrNull() ?: -1)
        }
    }

    private fun observeLogs() {
        lifecycleScope.launch {
            viewModel.logs.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

}