package io.github.qkeeper.todoapp.data.remote

import io.github.qkeeper.todoapp.data.remote.dto.TodoItemDto
import io.github.qkeeper.todoapp.data.remote.dto.TodoItemResponse
import io.github.qkeeper.todoapp.data.remote.dto.TodoListResponse
import retrofit2.Response
import retrofit2.http.*

interface TodoApi {
    @GET("list")
    suspend fun getList(): Response<TodoListResponse>

    @PATCH("list")
    suspend fun patchList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body list: Map<String, List<TodoItemDto>> // {"list": [...]}
    ): Response<TodoListResponse>

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body element: Map<String, TodoItemDto> // {"element": {...}}
    ): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body element: Map<String, TodoItemDto>
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<TodoItemResponse>
}