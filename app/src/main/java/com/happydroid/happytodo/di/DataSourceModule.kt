package com.happydroid.happytodo.di

import android.app.Application
import androidx.room.Room
import com.happydroid.happytodo.data.local.LocalStorage
import dagger.Module
import dagger.Provides

@Module
interface DataSourceModule {
    companion object{
        @AppScope
        @Provides
        fun provideLocalStorage(application: Application): LocalStorage {
            return Room.databaseBuilder(
                application,
                LocalStorage::class.java,
                "todo_database"
            ).build()
        }
    }
}
