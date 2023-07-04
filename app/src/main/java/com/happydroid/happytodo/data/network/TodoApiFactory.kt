package com.happydroid.happytodo.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

object TodoApiFactory {

    private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    private var okHttpClient: OkHttpClient
    private var gson : Gson

    init {
        val authInterceptor = AuthInterceptor()
        val revisionInterceptor = RevisionInterceptor()
        val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(authInterceptor)
            .addNetworkInterceptor(revisionInterceptor)
            .build()

        gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
    }

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()


    val retrofitService: TodoApiService by lazy { retrofit.create(TodoApiService::class.java) }
}