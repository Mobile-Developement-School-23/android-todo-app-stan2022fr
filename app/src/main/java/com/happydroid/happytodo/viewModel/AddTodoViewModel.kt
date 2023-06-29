package com.happydroid.happytodo.viewModel

import androidx.lifecycle.ViewModel
import com.happydroid.happytodo.data.TodoItem
import com.happydroid.happytodo.data.TodoItemsRepository

class AddTodoViewModel : ViewModel() {
    private val todoItemsRepository = TodoItemsRepository.getInstance()

    fun addTodoItem(todoItem: TodoItem) {
        todoItemsRepository.addTodoItem(todoItem)
    }

    fun deleteTodoItem(idTodoItem: String) {
        todoItemsRepository.deleteTodoItem(idTodoItem)
    }

    fun getTodoItem(idTodoItem: String) : TodoItem?{
        return todoItemsRepository.getTodoItem(idTodoItem)
    }
}