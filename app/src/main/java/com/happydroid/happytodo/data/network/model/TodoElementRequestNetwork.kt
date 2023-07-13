package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the request network model for a todo element.
 */
data class TodoElementRequestNetwork(
    @SerializedName("element") val element: TodoItemNetwork?
)
