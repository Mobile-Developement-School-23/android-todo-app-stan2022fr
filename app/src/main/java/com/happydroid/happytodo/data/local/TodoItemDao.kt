package com.happydroid.happytodo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.happydroid.happytodo.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing the todo item data access object.
 */
@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items")
    suspend fun getAll(): List<TodoItem>

    @Query("SELECT * FROM todo_items")
    fun observeAll(): Flow<List<TodoItem>>

    @Query("SELECT COUNT(*) FROM todo_items")
    suspend fun getTodoItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItem(todoItem: TodoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(todoItems: List<TodoItem>)

    @Update
    suspend fun editTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("DELETE FROM todo_items WHERE id =:idTodoItem")
    suspend fun deleteById(idTodoItem: String)
}
