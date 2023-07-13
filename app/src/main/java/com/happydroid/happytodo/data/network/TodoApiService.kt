package com.happydroid.happytodo.data.network

import com.happydroid.happytodo.data.network.model.TodoElementRequestNetwork
import com.happydroid.happytodo.data.network.model.TodoElementResponseNetwork
import com.happydroid.happytodo.data.network.model.TodoListRequestNetwork
import com.happydroid.happytodo.data.network.model.TodoListResponseNetwork
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interface for interacting with the Todo API service.
 */
interface TodoApiService {
    @GET("list")
    suspend fun fetchAll(): Response<TodoListResponseNetwork>

    @PATCH("list")
    suspend fun updateAll(@Body list: TodoListRequestNetwork): Response<TodoListResponseNetwork>


    @GET("list{id}")
    suspend fun getItem(@Path("id") id: String): Response<TodoElementResponseNetwork>

    @POST("list")
    suspend fun addItem(@Body element: TodoElementRequestNetwork): Response<TodoElementResponseNetwork>

    @PUT("list/{id}")
    suspend fun updateItem(
        @Path("id") id: String, @Body element: TodoElementRequestNetwork
    ): Response<TodoElementResponseNetwork>

    @DELETE("list/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<TodoElementResponseNetwork>

}


