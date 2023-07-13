package com.happydroid.happytodo.data.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This class is responsible for creating instances of the TodoApiService.
 */
abstract class TodoApiFactory {
    abstract val okHttpClient: OkHttpClient
    abstract val gson: Gson

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }

    val retrofitService: TodoApiService by lazy { retrofit.create(TodoApiService::class.java) }

    companion object {
        private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    }
}
