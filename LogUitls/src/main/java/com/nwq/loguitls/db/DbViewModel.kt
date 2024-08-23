package com.nwq.loguitls.db

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nwq.loguitls.LogLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class DbViewModel : ViewModel() {

    private val _tag = MutableStateFlow<String?>("")
    private val _level = MutableStateFlow(LogLevel.VERBOSE)
    private val _startTimeThreshold = MutableStateFlow(-1L)
    private val _endTimeThreshold = MutableStateFlow(Long.MAX_VALUE)
    private val _createTimeThreshold = MutableStateFlow(-1L)

    // Combine the parameters and get the Flow from getLogFlow
    val logs: Flow<PagingData<LogEntity>> = combine(
        _tag,
        _level, _startTimeThreshold, _endTimeThreshold, _createTimeThreshold
    ) { tag, level, start, end, create ->
        getTagLogFlow(tag, level, start, end, create)
    }.flatMapLatest { it }
        .cachedIn(viewModelScope)

    fun updateTag(tag: String?) {
        _tag.value = tag
    }

    fun updateLevel(@LogLevel level: Int) {
        _level.value = level
    }

    fun updateStartTimeThreshold(time: Long) {
        _startTimeThreshold.value = time
    }

    fun updateEndTimeThreshold(time: Long) {
        _endTimeThreshold.value = time
    }

    fun updateCreateTimeThreshold(time: Long) {
        _createTimeThreshold.value = time
    }


    private fun getLogFlow(
        level: Int = LogLevel.VERBOSE,
        startTimeThreshold: Long = -1,
        endTimeThreshold: Long = Long.MAX_VALUE,
        createTimeThreshold: Long = -1
    ): Flow<PagingData<LogEntity>> {
        val dao = LogDatabase.getDatabase().logDao()
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                dao.queryByTimePaged(
                    level,
                    startTimeThreshold,
                    endTimeThreshold,
                    createTimeThreshold,
                    10,
                    0
                )
            }
        ).flow
    }


    private fun getTagLogFlow(
        tag: String? = null,
        level: Int = LogLevel.VERBOSE,
        startTimeThreshold: Long = -1,
        endTimeThreshold: Long = Long.MAX_VALUE,
        createTimeThreshold: Long = -1
    ): Flow<PagingData<LogEntity>> {
        if (TextUtils.isEmpty(tag)) {
            return getLogFlow(level, startTimeThreshold, endTimeThreshold, createTimeThreshold)
        }
        val dao = LogDatabase.getDatabase().logDao()
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                dao.queryByTagPaged(
                    tag!!,
                    level,
                    startTimeThreshold,
                    endTimeThreshold,
                    createTimeThreshold,
                    10,
                    0
                )
            }
        ).flow
    }






}