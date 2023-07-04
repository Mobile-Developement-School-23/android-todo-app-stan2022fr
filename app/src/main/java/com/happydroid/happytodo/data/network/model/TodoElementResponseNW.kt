package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

data class TodoElementResponseNW(
    @SerializedName("revision") val revision: Int?,
    @SerializedName("element") val element: TodoItemNW?,
    @SerializedName("status") val status: String?,
)