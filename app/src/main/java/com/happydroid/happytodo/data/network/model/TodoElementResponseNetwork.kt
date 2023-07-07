package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the response network model for a todo element.
 */
data class TodoElementResponseNetwork(
    @SerializedName("revision") override val revision: Int?,
    @SerializedName("element") val element: TodoItemNetwork?,
    @SerializedName("status") override val status: String?,
) : ResponseNetwork

