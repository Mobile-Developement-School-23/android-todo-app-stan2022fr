package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the request network model for a list of todos.
 */
data class TodoListRequestNetwork(
    @SerializedName("list") val list: List<TodoItemNetwork>?,
)
