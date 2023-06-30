package com.happydroid.happytodo.viewModel

import androidx.lifecycle.ViewModel
import com.happydroid.happytodo.data.TodoItem
import com.happydroid.happytodo.data.TodoItemsRepository
import java.util.Date
import java.util.UUID

class AddTodoViewModel : ViewModel() {
    private val todoItemsRepository = TodoItemsRepository.getInstance()

    fun addOrUpdateTodoItem(oldId : String?, todoText : String, priority : TodoItem.Priority, deadline : Date?) {
        val newTodoItem: TodoItem
        if (oldId != null){
            val oldTodoItem = todoItemsRepository.getTodoItem(oldId)
            newTodoItem = oldTodoItem?.copy(text = todoText, priority = priority, deadline = deadline, modifiedDate = Date())
                ?: return
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
        todoItemsRepository.addOrUpdateTodoItem(newTodoItem)
    }

    fun deleteTodoItem(idTodoItem: String) {
        todoItemsRepository.deleteTodoItem(idTodoItem)
    }

    fun getTodoItem(idTodoItem: String) : TodoItem?{
        return todoItemsRepository.getTodoItem(idTodoItem)
    }

}