package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

data class TodoElementRequestNW(
    @SerializedName("element") val element: TodoItemNW?
)