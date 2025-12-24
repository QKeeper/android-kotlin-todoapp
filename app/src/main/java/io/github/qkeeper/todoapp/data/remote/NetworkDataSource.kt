package io.github.qkeeper.todoapp.data.remote

import io.github.qkeeper.todoapp.TodoItem
import io.github.qkeeper.todoapp.data.remote.dto.TodoItemDto
import kotlinx.coroutines.delay
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class NetworkDataSource {
    private val api = NetworkModule.api

    private suspend fun <T> safeApiCall(
        block: suspend () -> Response<T>
    ): T? {
        var currentDelay = 1000L
        val maxAttempts = 3
        repeat(maxAttempts) { attempt ->
            try {
                val response = block()
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    if (response.code() in 500..599) {
                        Timber.w("Server error ${response.code()}. Retrying...")
                    } else {
                        Timber.e("Client error ${response.code()}")
                        return null
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Network exception on attempt ${attempt + 1}")
                if (attempt == maxAttempts - 1) throw e
            }
            delay(currentDelay)
            currentDelay = (currentDelay * 1.5).toLong()
        }
        return null
    }

    suspend fun loadItems(): Pair<List<TodoItem>, Int> {
        val response = safeApiCall { api.getList() }
        val items = response?.list?.map { it.toDomain() } ?: emptyList()
        val revision = response?.revision ?: 0
        return items to revision
    }

    suspend fun addItem(revision: Int, item: TodoItem): Int {
        val dto = TodoItemDto.fromDomain(item)
        val response = safeApiCall {
            api.addItem(revision, mapOf("element" to dto))
        }
        return response?.revision ?: revision
    }

    suspend fun updateItem(revision: Int, item: TodoItem): Int {
        val dto = TodoItemDto.fromDomain(item)
        val response = safeApiCall {
            api.updateItem(revision, item.uid, mapOf("element" to dto))
        }
        return response?.revision ?: revision
    }

    suspend fun deleteItem(revision: Int, id: String): Int {
        val response = safeApiCall {
            api.deleteItem(revision, id)
        }
        return response?.revision ?: revision
    }
}