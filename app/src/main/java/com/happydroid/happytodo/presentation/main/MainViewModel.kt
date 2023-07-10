package com.happydroid.happytodo.presentation.main

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItemsResult
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class is responsible for managing the main view of the application and interacting with the todo items repository.
 */
class MainViewModel @Inject constructor(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    private val _todoItemsResult = MutableStateFlow(TodoItemsResult())
    val todoItemsResult: StateFlow<TodoItemsResult> = _todoItemsResult
    var showOnlyUnfinishedItems: Boolean = false
    private var job: Job? = null

    fun startObservingLifecycle(lifecycle: Lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                job = viewModelScope.launch {
                    todoItemsRepository.todoItemsResult.collect { todoItems ->
                        _todoItemsResult.value = todoItems
                    }
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                job?.cancel()
            }
        }
        lifecycle.addObserver(observer)
    }


    fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        viewModelScope.launch {
            todoItemsRepository.changeStatusTodoItem(idTodoItem, isDone)
        }
    }

    fun fetchFromRemote() {
        viewModelScope.launch {
            todoItemsRepository.fetchFromRemoteApi()
        }
    }

    fun onErrorDismiss(messageId: ErrorCode) {
        viewModelScope.launch {
            todoItemsRepository.removeMessageFromQueue(messageId)
        }
    }


}

