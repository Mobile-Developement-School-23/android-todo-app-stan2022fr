package com.happydroid.happytodo.di

import android.app.Application
import androidx.room.Room
import com.happydroid.happytodo.data.local.LocalStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataSourceModule {

    @Singleton
    @Provides
    fun provideLocalStorage(application: Application): LocalStorage {
        return Room.databaseBuilder(
            application,
            LocalStorage::class.java,
            "todo_database"
        ).build()
    }


}
