package com.happydroid.happytodo.di

import com.happydroid.happytodo.ToDoApplication
import dagger.Component
import javax.inject.Scope

@Scope
annotation class AppScope

@AppScope
@Component(modules = [AppModule::class, DataSourceModule::class, NetworkModule::class])
interface AppComponent {

    fun injectTo(application: ToDoApplication)

    fun mainFragmentComponent(): MainFragmentComponent

    fun addTodoFragmentComponent(): AddTodoFragmentComponent

}

