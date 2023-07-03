package com.happydroid.happytodo.data.repository
import com.happydroid.happytodo.data.datasource.HardCodedDataSource
import com.happydroid.happytodo.data.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class TodoItemsRepository private constructor(){

    private val dataSource : HardCodedDataSource= HardCodedDataSource()
    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems: StateFlow<List<TodoItem>> = _todoItems

    init {
        CoroutineScope(Dispatchers.Main).launch {
            updateTodoItems()
        }
    }


    suspend fun updateTodoItems() {
        val loadedList = withContext(Dispatchers.IO) { dataSource.loadTodoItems() }
        withContext(Dispatchers.Main) {
            _todoItems.value = loadedList
        }
    }


    suspend fun deleteTodoItem(idTodoItem: String) {
        withContext(Dispatchers.Default) {
            _todoItems.value = todoItems.value.filter { it.id != idTodoItem }
        }

    }


    suspend fun getTodoItem(idTodoItem: String): TodoItem? {
        return withContext(Dispatchers.Default) { todoItems.value.find { it.id == idTodoItem }}
    }

    suspend fun getTodoItems(): List<TodoItem> {
        return withContext(Dispatchers.Default) { todoItems.value }
    }

    suspend fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItems = withContext(Dispatchers.Default) {
            todoItems.value.map { todoItem ->
                if (todoItem.id == idTodoItem) todoItem.copy(isDone = isDone, modifiedDate = Date())
                else todoItem
            }
        }
        withContext(Dispatchers.Main){
            _todoItems.value = newItems
        }
    }

    suspend fun addOrUpdateTodoItem(todoItem: TodoItem) {
        val newItems = withContext(Dispatchers.Default) {

            val currentList = todoItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == todoItem.id }
            if (index != -1) {
                // Элемент найден, перезаписываем его
                currentList[index] = todoItem
            } else {
                currentList.add(todoItem)
            }
            currentList.toList()
        }

        withContext(Dispatchers.Main){
            _todoItems.value = newItems
        }
    }

    suspend fun updateTodoItem(newTodoItem: TodoItem)  {
        val newItems = withContext(Dispatchers.Default) {
            todoItems.value.map { todoItem ->
                if (todoItem.id == newTodoItem.id) newTodoItem
                else todoItem
            }
        }
        withContext(Dispatchers.Main){
            _todoItems.value = newItems
        }
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
