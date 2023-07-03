package com.happydroid.happytodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val todoItemsRepository = TodoItemsRepository.getInstance()
    private val _todoItems = MediatorLiveData<List<TodoItem>>(emptyList())
    val todoItems: LiveData<List<TodoItem>> = _todoItems
    var showOnlyUnfinishedItems : Boolean = false

    init{
        _todoItems.addSource(todoItemsRepository.todoItems){
            todoItems -> _todoItems.value = todoItems
        }
    }

    fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean){
        viewModelScope.launch {
            todoItemsRepository.changeStatusTodoItem(idTodoItem, isDone)
        }
    }


}

