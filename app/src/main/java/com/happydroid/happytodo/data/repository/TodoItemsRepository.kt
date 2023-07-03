package com.happydroid.happytodo.data.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.happydroid.happytodo.data.datasource.HardCodedDataSource
import com.happydroid.happytodo.data.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class TodoItemsRepository private constructor(){

    private val dataSource : HardCodedDataSource= HardCodedDataSource()
    private val _todoItems = MutableLiveData<List<TodoItem>>(emptyList())
    val todoItems: LiveData<List<TodoItem>> = _todoItems

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
            _todoItems.postValue(todoItems.value.orEmpty().filter { it.id != idTodoItem })
        }

    }


    suspend fun getTodoItem(idTodoItem: String): TodoItem? {
        return withContext(Dispatchers.Default) {todoItems.value.orEmpty().find { it.id == idTodoItem }}
    }

    suspend fun getTodoItems(): List<TodoItem> {
        return withContext(Dispatchers.Default) {todoItems.value.orEmpty()}
    }

    suspend fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItems = withContext(Dispatchers.Default) {
            todoItems.value.orEmpty().map { todoItem ->
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

            val currentList = todoItems.value.orEmpty().toMutableList()
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
            todoItems.value.orEmpty().map { todoItem ->
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
