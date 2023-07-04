package com.happydroid.happytodo.data.model

import com.happydroid.happytodo.R

data class TodoResult(
    val data: List<TodoItem> = emptyList(),
    val errorMessages: List<ErrorCode> = emptyList()
)
enum class ErrorCode(val stringResId: Int) {
    LOAD_FROM_HARDCODED_DATASOURCE(R.string.load_from_hardcoded_datasource),
    LOAD_FROM_LOCAL(R.string.load_from_local_datasource),
    LOAD_FROM_REMOTE(R.string.load_from_remote_datasource),
    NO_CONNECTION(R.string.no_connection),
    UNKNOW_ERROR(R.string.unknow_error)
}