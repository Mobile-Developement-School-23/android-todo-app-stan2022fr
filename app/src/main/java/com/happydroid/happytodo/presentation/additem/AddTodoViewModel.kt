package com.happydroid.happytodo.presentation.additem

import android.util.Log
import androidx.lifecycle.ViewModel
import com.happydroid.happytodo.data.model.TodoItem
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * This class is responsible for handling the logic related to editing or adding a new todo item.
 */
class AddTodoViewModel @Inject constructor(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    private val customScope = CoroutineScope(Dispatchers.IO)

    fun addOrUpdateTodoItem(
        oldId: String?,
        todoText: String,
        priority: TodoItem.Priority,
        deadline: Date?
    ) {
        customScope.launch {
            val newTodoItem: TodoItem?

            if (oldId != null) {
                val oldTodoItem = todoItemsRepository.getTodoItem(oldId)
                newTodoItem = oldTodoItem?.copy(
                    text = todoText,
                    priority = priority,
                    deadline = deadline,
                    modifiedDate = Date()
                )
                newTodoItem?.let{todoItemsRepository.updateTodoItem(newTodoItem)}
            } else {
                newTodoItem = TodoItem(
                    id = UUID.randomUUID().toString(),
                    text = todoText,
                    priority = priority,
                    deadline = deadline,
                    isDone = false,
                    createdDate = Date(),
                    modifiedDate = Date()
                )
                todoItemsRepository.addTodoItem(newTodoItem)
            }
        }
    }


    fun deleteTodoItem(idTodoItem: String) {
        customScope.launch {
            todoItemsRepository.deleteTodoItem(idTodoItem)
        }
    }

    suspend fun getTodoItem(idTodoItem: String): TodoItem? {
        return todoItemsRepository.getTodoItem(idTodoItem)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("HappyTodo", "AddTodoViewModel onCleared")

    }

}
