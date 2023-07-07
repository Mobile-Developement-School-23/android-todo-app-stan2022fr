package com.happydroid.happytodo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.happydroid.happytodo.data.local.DateConverter
import com.happydroid.happytodo.data.network.model.RevisionHolder
import com.happydroid.happytodo.data.network.model.TodoElementRequestNetwork
import com.happydroid.happytodo.data.network.model.TodoItemNetwork
import java.util.Date

/**
 * Represents a todo item.
 */
@Entity(tableName = "todo_items")
@TypeConverters(DateConverter::class)
data class TodoItem(
    @PrimaryKey val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val modifiedDate: Date?
) {

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

fun TodoItem.toTodoItemNetwork(): TodoItemNetwork {
    return TodoItemNetwork(
        id = id,
        text = text,
        importance = priority.value,
        deadline = deadline,
        done = isDone,
        color = null,
        changedAt = modifiedDate ?: createdDate,
        createdAt = createdDate,
        last_updated_by = RevisionHolder.deviceId
    )
}

fun TodoItem.toTodoElementRequestNW(): TodoElementRequestNetwork {
    return TodoElementRequestNetwork(this.toTodoItemNetwork())
}
