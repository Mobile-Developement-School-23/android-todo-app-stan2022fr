package com.happydroid.happytodo.data

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val modifiedDate: Date?
) {
    enum class Priority {
        LOW, NORMAL, HIGH
    }
}