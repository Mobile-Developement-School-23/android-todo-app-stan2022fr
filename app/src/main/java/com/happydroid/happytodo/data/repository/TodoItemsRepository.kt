package com.happydroid.happytodo.data.repository
import com.happydroid.happytodo.data.datasource.HardCodedDataSource
import com.happydroid.happytodo.data.model.TodoItem
import java.util.Date

class TodoItemsRepository private constructor(){
    private val todoItems: MutableList<TodoItem> = mutableListOf()
    private val dataSource : HardCodedDataSource= HardCodedDataSource()

    init {
        // Заглушка для списка дел
        todoItems.addAll(dataSource.loadTodoItems())
    }

    fun getTodoItems(): List<TodoItem> {
        return todoItems.toList()
    }

    fun addOrUpdateTodoItem(todoItem: TodoItem) {
        val index = todoItems.indexOfFirst { it.id == todoItem.id }
        if (index != -1) {
            // Элемент найден, перезаписываем его
            todoItems[index] = todoItem
        } else {
            // Элемент не найден, добавляем новый элемент
            todoItems.add(todoItem)
        }
    }

    fun deleteTodoItem(idTodoItem: String) {
        val index = todoItems.indexOfFirst { it.id == idTodoItem }
        if (index != -1) {
            todoItems.removeAt(index)
        }
    }


    fun getTodoItem(idTodoItem: String): TodoItem? {
        return todoItems.find { it.id == idTodoItem }
    }

    fun changeStatusTodoItem(idTodoItem: String, isDone: Boolean) {
        val newItem = todoItems.find { it.id == idTodoItem }?.copy(isDone = isDone, modifiedDate = Date())
        if (newItem != null){
            addOrUpdateTodoItem(newItem)
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
