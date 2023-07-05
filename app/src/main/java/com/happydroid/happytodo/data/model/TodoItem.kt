package com.happydroid.happytodo.data.model

import com.happydroid.happytodo.data.network.model.RevisionHolder
import com.happydroid.happytodo.data.network.model.TodoElementRequestNW
import com.happydroid.happytodo.data.network.model.TodoItemNW
import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val modifiedDate: Date?
){

    enum class Priority(val value: String? = "basic") {
        NORMAL("basic"),
        LOW("low"),
        HIGH("important");

        companion object {
            fun fromString(value: String? = "basic"): Priority {
                return when (value) {
                    "low" -> LOW
                    "basic" -> NORMAL
                    "important" -> HIGH
                    else -> NORMAL
                }
            }
        }
    }
}

fun TodoItem.toTodoItemNW(): TodoItemNW {
    return TodoItemNW(
        id = id,
        text = text,
        importance = priority.value,
        deadline = deadline,
        done = isDone,
        color = null,
        changedAt = modifiedDate?: createdDate,
        createdAt = createdDate,
        last_updated_by = RevisionHolder.deviceId
    )
}

fun TodoItem.toTodoElementRequestNW(): TodoElementRequestNW {
    return TodoElementRequestNW(this.toTodoItemNW())
}