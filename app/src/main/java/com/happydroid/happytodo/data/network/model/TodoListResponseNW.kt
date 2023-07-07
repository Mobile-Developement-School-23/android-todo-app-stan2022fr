package com.happydroid.happytodo.data.network.model

import com.google.gson.annotations.SerializedName
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.model.TodoResult
import java.util.Date

data class TodoListResponseNW(
    @SerializedName("status") override val status: String?,
    @SerializedName("list") val list: List<TodoItemNW>?,
    @SerializedName("revision") override val revision: Int?
) : ResponseNW

fun TodoListResponseNW.toTodoResult(errorMessages : List<ErrorCode>): TodoResult {
    val todoItems: List<TodoItem> = this.list?.map { todoItemNW ->
        TodoItem(
            id = todoItemNW.id ?: "",
            text = todoItemNW.text ?: "",
            priority = TodoItem.Priority.fromString(todoItemNW.importance) ,
            deadline = todoItemNW.deadline,
            isDone = todoItemNW.done ?: false,
            modifiedDate = todoItemNW.changedAt,
            createdDate = todoItemNW.createdAt ?: Date(),
        )
    } ?: emptyList()

    return TodoResult(data = todoItems, errorMessages)
}