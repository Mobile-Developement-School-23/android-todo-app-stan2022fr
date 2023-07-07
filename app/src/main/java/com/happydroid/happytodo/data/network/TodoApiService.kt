package com.happydroid.happytodo.data.network

import com.happydroid.happytodo.data.network.model.TodoElementRequestNW
import com.happydroid.happytodo.data.network.model.TodoElementResponseNW
import com.happydroid.happytodo.data.network.model.TodoListRequestNW
import com.happydroid.happytodo.data.network.model.TodoListResponseNW
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TodoApiService {
    @GET("list")
    suspend fun fetchAll(): Response<TodoListResponseNW>

    @PATCH("list")
    suspend fun updateAll(@Body list: TodoListRequestNW): Response<TodoListResponseNW>


    @GET("list{id}")
    suspend fun getItem(@Path("id") id: String): Response<TodoElementResponseNW>

    @POST("list")
    suspend fun addItem(@Body element: TodoElementRequestNW): Response<TodoElementResponseNW>

    @PUT("list/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body element: TodoElementRequestNW
    ): Response<TodoElementResponseNW>

    @DELETE("list/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<TodoElementResponseNW>

}


