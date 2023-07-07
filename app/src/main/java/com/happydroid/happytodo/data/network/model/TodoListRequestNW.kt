package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

data class TodoListRequestNW(
    @SerializedName("list") val list: List<TodoItemNW>?,
)