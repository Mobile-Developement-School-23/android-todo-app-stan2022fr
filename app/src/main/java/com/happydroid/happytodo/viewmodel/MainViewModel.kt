package com.happydroid.happytodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoResult
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val todoItemsRepository = TodoItemsRepository.getInstance()
    private val _todoItemsResult = MutableStateFlow(TodoResult())
    val todoItemsResult: StateFlow<TodoResult> = _todoItemsResult
    var showOnlyUnfinishedItems : Boolean = false

    init{
        viewModelScope.launch {
            todoItemsRepository.todoItemsResult.collect { todoItems ->
                _todoItemsResult.value = todoItems
            }
        }
    }

    fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean){
        viewModelScope.launch {
            todoItemsRepository.changeStatusTodoItem(idTodoItem, isDone)
        }
    }

    fun fetchFromRemote(){
        viewModelScope.launch {
            todoItemsRepository.fetchFromRemote()
        }
    }

    fun onErrorDismiss(messageId : ErrorCode){
        viewModelScope.launch {
            todoItemsRepository.onErrorDismiss(messageId)
        }
    }


}

