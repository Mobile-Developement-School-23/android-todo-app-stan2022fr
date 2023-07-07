package com.happydroid.happytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class AddTodoViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        Log.i("HappyTodo", "AddTodoViewModel onCleared")

    }
    fun addOrUpdateTodoItem(oldId : String?, todoText : String, priority : TodoItem.Priority, deadline : Date?) {
        GlobalScope.launch {
            val newTodoItem: TodoItem?

            if (oldId != null){
                val oldTodoItem = todoItemsRepository.getTodoItem(oldId)
                newTodoItem = oldTodoItem?.copy(text = todoText, priority = priority, deadline = deadline, modifiedDate = Date())
            } else{
                newTodoItem = TodoItem(
                   id = UUID.randomUUID().toString(),
                   text = todoText,
                   priority = priority,
                   deadline = deadline,
                   isDone = false,
                   createdDate = Date(),
                   modifiedDate = Date()
               )
            }
            newTodoItem?.let { todoItemsRepository.addOrUpdateTodoItem(it) }
        }
    }


     fun deleteTodoItem(idTodoItem: String) {
        GlobalScope.launch {
            todoItemsRepository.deleteTodoItem(idTodoItem)
        }
    }

    suspend fun getTodoItem(idTodoItem: String) : TodoItem?{
        return todoItemsRepository.getTodoItem(idTodoItem)
    }

}