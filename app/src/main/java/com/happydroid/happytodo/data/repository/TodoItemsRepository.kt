package com.happydroid.happytodo.data.repository

import android.util.Log
import com.happydroid.happytodo.data.datasource.HardCodedDataSource
import com.happydroid.happytodo.data.model.ErrorCode
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.model.TodoResult
import com.happydroid.happytodo.data.network.TodoApiFactory
import com.happydroid.happytodo.data.network.model.TodoListResponseNW
import com.happydroid.happytodo.data.network.model.toTodoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Date

class TodoItemsRepository private constructor(){

    private val dataSource : HardCodedDataSource= HardCodedDataSource()
    private val apiRemote = TodoApiFactory.retrofitService
    private val _todoItemsResult = MutableStateFlow(TodoResult())
    val todoItemsResult: StateFlow<TodoResult> = _todoItemsResult

    init {
        CoroutineScope(Dispatchers.IO).launch {
            updateTodoItems()
            fetchFromRemote()

        // загрузка на сервер НЕ РАБОТАЕТ - crash
        // apiRemote.updateAll(todoItemsResult.value.data.map { it.toTodoItemNW() })

        }
    }


    suspend fun updateTodoItems() {
        val loadedList = withContext(Dispatchers.IO) { dataSource.loadTodoItems() }
        withContext(Dispatchers.Main) {
            _todoItemsResult.value = TodoResult(loadedList, listOf(ErrorCode.LOAD_FROM_HARDCODED_DATASOURCE))
        }
    }

    suspend fun fetchFromRemote() {
        Log.i("hhh", "TodoItemsRepository.fetchFromRemote()")
        withContext(Dispatchers.IO) {
            try {
                val response: Response<TodoListResponseNW> = apiRemote.fetchAll()

                if (response.isSuccessful) {
                    val todoListResponseNW: TodoListResponseNW? = response.body()
                    Log.i("hhh", todoListResponseNW.toString())
                    _todoItemsResult.value = todoListResponseNW?.toTodoResult(listOf(ErrorCode.LOAD_FROM_REMOTE)) ?: TodoResult(
                        emptyList(), listOf(ErrorCode.UNKNOW_ERROR)
                    )

                } else {
                    _todoItemsResult.value = TodoResult(todoItemsResult.value.data, listOf(ErrorCode.NO_CONNECTION))
                    val errorMessage = response.errorBody()?.string()
                    Log.e("TodoItemsRepository", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                Log.e("TodoItemsRepository", "Exception: ${e.message}")
            }
        }

    }

    fun onErrorDismiss(messageId : ErrorCode){
        Log.i("hhh", "TodoItemsRepository.onErrorDismiss()")
        _todoItemsResult.update { todoItemsResult ->
            val errorMessages = todoItemsResult.errorMessages
                .filterNot { it.stringResId == messageId.stringResId}
            todoItemsResult.copy(errorMessages = errorMessages)
        }
    }

    suspend fun deleteTodoItem(idTodoItem: String) {
        val newItems = withContext(Dispatchers.Default) {
            _todoItemsResult.value.data.filter { it.id != idTodoItem }
        }
        _todoItemsResult.value = _todoItemsResult.value.copy(data = newItems)

    }


    suspend fun getTodoItem(idTodoItem: String): TodoItem? {
        return withContext(Dispatchers.Default) {
            todoItemsResult.value.data.find { it.id == idTodoItem }
        }
    }

    suspend fun getTodoItems(): List<TodoItem> {
        return withContext(Dispatchers.Default) { todoItemsResult.value.data }
    }

    suspend fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItems = withContext(Dispatchers.Default) {
            todoItemsResult.value.data.map { todoItem ->
                if (todoItem.id == idTodoItem) todoItem.copy(isDone = isDone, modifiedDate = Date())
                else todoItem
            }
        }
        _todoItemsResult.value = TodoResult(newItems)

    }

    suspend fun addOrUpdateTodoItem(todoItem: TodoItem) {
        val newItems = withContext(Dispatchers.Default) {

            val currentList = todoItemsResult.value.data.toMutableList()
            val index = currentList.indexOfFirst { it.id == todoItem.id }
            if (index != -1) {
                // Элемент найден, перезаписываем его
                currentList[index] = todoItem
            } else {
                currentList.add(todoItem)
            }
            currentList.toList()
        }
        _todoItemsResult.value = TodoResult(newItems)

    }

    suspend fun updateTodoItem(newTodoItem: TodoItem)  {
        val newItems = withContext(Dispatchers.Default) {
            todoItemsResult.value.data.map { todoItem ->
                if (todoItem.id == newTodoItem.id) newTodoItem
                else todoItem
            }
        }
        _todoItemsResult.value = TodoResult(newItems)
    }


    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null

        fun getInstance(): TodoItemsRepository {
            return instance ?: synchronized(this) {
                instance ?: TodoItemsRepository().also { instance = it }
            }
        }
    }
}
