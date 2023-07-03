package com.happydroid.happytodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val todoItemsRepository = TodoItemsRepository.getInstance()
    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems
    var showOnlyUnfinishedItems : Boolean = false

    init{
        viewModelScope.launch {
            todoItemsRepository.todoItems.collect { todoItems ->
                _todoItems.value = todoItems
            }
        }
    }

    fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean){
        viewModelScope.launch {
            todoItemsRepository.changeStatusTodoItem(idTodoItem, isDone)
        }
    }


}

