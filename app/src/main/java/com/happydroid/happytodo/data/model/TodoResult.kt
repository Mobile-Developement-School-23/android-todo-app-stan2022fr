package com.happydroid.happytodo.data.model

import com.happydroid.happytodo.R

data class TodoResult(
    val data: List<TodoItem> = emptyList(),
    val errorMessages: List<ErrorCode> = emptyList()
)
enum class ErrorCode(val stringResId: Int) {
    LOAD_FROM_HARDCODED_DATASOURCE(R.string.load_from_fake_datasource),
    LOAD_FROM_LOCAL(R.string.load_from_local_datasource),
    LOAD_FROM_REMOTE(R.string.load_from_remote_datasource),
    NO_CONNECTION(R.string.no_connection),
    ERROR_401(R.string.error_401),
    ERROR_404(R.string.error_404),
    ERROR_500(R.string.error_500),
    UNKNOW_ERROR(R.string.unknow_error)
}

