package com.happydroid.happytodo.di

import com.happydroid.happytodo.presentation.additem.AddTodoFragment
import com.happydroid.happytodo.presentation.main.MainFragment
import dagger.Subcomponent

@Subcomponent(modules = [ViewModelFactoryModule::class])
interface MainFragmentComponent {
    fun inject(mainFragment: MainFragment)
}

@Subcomponent(modules = [ViewModelFactoryModule::class])
interface AddTodoFragmentComponent {
    fun inject(addTodoFragment: AddTodoFragment)
}
