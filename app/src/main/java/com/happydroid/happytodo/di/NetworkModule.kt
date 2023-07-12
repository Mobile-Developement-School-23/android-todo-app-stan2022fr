package com.happydroid.happytodo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.happydroid.happytodo.data.network.AuthInterceptor
import com.happydroid.happytodo.data.network.DateTypeAdapter
import com.happydroid.happytodo.data.network.RevisionInterceptor
import com.happydroid.happytodo.data.network.TodoApiFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Date
@Module
interface NetworkModule {
    companion object{
        @Provides
        @AppScope
        fun provideOkHttpClient(): OkHttpClient {
            val authInterceptor = AuthInterceptor()
            val revisionInterceptor = RevisionInterceptor()
            val httpLoggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(authInterceptor)
                .addNetworkInterceptor(revisionInterceptor)
                .build()
        }

        @Provides
        @AppScope
        fun provideGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateTypeAdapter())
                .create()
        }

        @Provides
        @AppScope
        fun provideTodoApiFactory(okHttpClient: OkHttpClient, gson: Gson): TodoApiFactory {
            return object : TodoApiFactory() {
                override val okHttpClient = okHttpClient
                override val gson = gson
            }
        }
    }
}
