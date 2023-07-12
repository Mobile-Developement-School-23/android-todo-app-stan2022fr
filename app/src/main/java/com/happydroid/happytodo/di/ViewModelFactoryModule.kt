package com.happydroid.happytodo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happydroid.happytodo.data.repository.TodoItemsRepository
import com.happydroid.happytodo.presentation.additem.AddTodoViewModel
import com.happydroid.happytodo.presentation.main.MainViewModel
import dagger.Module
import dagger.Provides


@Module
interface ViewModelFactoryModule {
    companion object{
        @FragmentScope
        @Provides
        fun provideViewModelFactory(repository: TodoItemsRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        return MainViewModel(repository) as T
                    }else if (modelClass.isAssignableFrom(AddTodoViewModel::class.java)) {
                        return AddTodoViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}

