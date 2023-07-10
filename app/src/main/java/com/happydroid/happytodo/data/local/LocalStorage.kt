package com.happydroid.happytodo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.happydroid.happytodo.data.model.TodoItem

/**
 * Represents the local storage for todo items.
 */
@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
abstract class LocalStorage : RoomDatabase() {
    abstract fun todoItems(): TodoItemDao

}
