package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.model.TodoItemsResult
import java.util.Date

/**
 * Represents the response network model for a list of todos.
 */
data class TodoListResponseNetwork(
    @SerializedName("status") override val status: String?,
    @SerializedName("list") val list: List<TodoItemNetwork>?,
    @SerializedName("revision") override val revision: Int?
) : ResponseNetwork

fun TodoListResponseNetwork.toTodoItemsResult(errorMessages: List<ErrorCode>): TodoItemsResult {
    val todoItems: List<TodoItem> = this.list?.map { todoItemNW ->
        TodoItem(
            id = todoItemNW.id ?: "",
            text = todoItemNW.text ?: "",
            priority = TodoItem.Priority.fromString(todoItemNW.importance),
            deadline = todoItemNW.deadline,
            isDone = todoItemNW.done ?: false,
            modifiedDate = todoItemNW.changedAt,
            createdDate = todoItemNW.createdAt ?: Date(),
        )
    } ?: emptyList()

    return TodoItemsResult(data = todoItems, errorMessages)
}
