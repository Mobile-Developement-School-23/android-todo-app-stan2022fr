package com.happydroid.happytodo.di

import com.happydroid.happytodo.presentation.additem.AddTodoFragment
import com.happydroid.happytodo.presentation.main.MainFragment
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class FragmentScope

@FragmentScope
@Subcomponent(modules = [ViewModelFactoryModule::class])
interface MainFragmentComponent {
    fun inject(mainFragment: MainFragment)
}

@FragmentScope
@Subcomponent(modules = [ViewModelFactoryModule::class])
interface AddTodoFragmentComponent {
    fun inject(addTodoFragment: AddTodoFragment)
}
