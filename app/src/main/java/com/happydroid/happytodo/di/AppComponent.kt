package com.happydroid.happytodo.di

import com.happydroid.happytodo.data.repository.TodoItemsRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataSourceModule::class, NetworkModule::class])
interface AppComponent {
    fun mainFragmentComponent(): MainFragmentComponent

    fun addTodoFragmentComponent(): AddTodoFragmentComponent

    fun todoItemsRepository(): TodoItemsRepository
}

