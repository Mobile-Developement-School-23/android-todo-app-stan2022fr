package com.happydroid.happytodo.data.model

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

    enum class Priority {
        NORMAL, LOW,  HIGH;

        companion object {
            fun fromString(value: String): Priority {
                return when (value) {

                    "@string/priority_none" -> NORMAL
                    "@string/priority_low" -> LOW
                    "@string/priority_high" -> HIGH
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
        importance = priority.toString(),
        deadline = deadline,
        done = isDone,
        color = null,
        changedAt = modifiedDate,
        createdAt = createdDate,
        last_updated_by = "Device"
    )
}