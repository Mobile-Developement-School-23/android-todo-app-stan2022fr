package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Represents the network model for a todo item.
 */
data class TodoItemNetwork(
    @SerializedName("id") val id: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("importance") val importance: String?,
    @SerializedName("deadline") val deadline: Date?,
    @SerializedName("done") val done: Boolean?,
    @SerializedName("color") val color: String?,
    @SerializedName("changed_at") val changedAt: Date?,
    @SerializedName("created_at") val createdAt: Date?,
    @SerializedName("last_updated_by") val last_updated_by: String?,
)
