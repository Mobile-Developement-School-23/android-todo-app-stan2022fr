package com.happydroid.happytodo.di

import android.app.Application
import com.happydroid.happytodo.data.datasource.FakeDataSource
import com.happydroid.happytodo.data.local.LocalStorage
import com.happydroid.happytodo.data.local.TodoItemDao
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import dagger.Module
import dagger.Provides

@Module
class DataSourceModule(private val application: Application) {

    @Provides
    fun provideTodoItemsRepository(application: Application): TodoItemsRepository {
        return TodoItemsRepository.getInstance(application)
    }

    @Provides
    fun provideFakeDataSource(): FakeDataSource {
        return FakeDataSource()
    }

    @Provides
    fun provideTodoItemDao(): TodoItemDao {
        return LocalStorage.getDatabase(application).todoItems()
    }





}
