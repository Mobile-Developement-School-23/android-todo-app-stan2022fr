package com.happydroid.happytodo.di

import com.happydroid.happytodo.data.network.TodoApiFactory
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import com.happydroid.happytodo.presentation.additem.AddTodoFragment
import com.happydroid.happytodo.presentation.main.MainFragment
import dagger.Component

@Component(modules = [DataSourceModule::class])
interface AppComponent {

    fun inject(repository: TodoItemsRepository)
    fun inject(fragment: MainFragment)
    fun inject(fragment: AddTodoFragment)
    fun inject(todoApiFactory: TodoApiFactory)

}

