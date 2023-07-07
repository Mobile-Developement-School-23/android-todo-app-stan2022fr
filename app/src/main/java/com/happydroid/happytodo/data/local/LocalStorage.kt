package com.happydroid.happytodo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.happydroid.happytodo.data.model.TodoItem

/**
 * Represents the local storage for todo items.
 */
@Database(entities = [TodoItem::class], version = 1)
abstract class LocalStorage : RoomDatabase() {
    abstract fun todoItems(): TodoItemDao

    companion object {
        @Volatile
        private var INSTANCE: LocalStorage? = null

        fun getDatabase(context: Context): LocalStorage {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalStorage::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
