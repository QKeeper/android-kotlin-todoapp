// NetworkDataSource.kt
package io.github.qkeeper.todoapp.data.remote

import io.github.qkeeper.todoapp.TodoItem
import kotlinx.coroutines.delay
import timber.log.Timber

class NetworkDataSource {

    suspend fun loadItems(): List<TodoItem> {
        delay(2000)
        Timber.d("Network: List loaded")
        return emptyList()
    }

    suspend fun addItem(item: TodoItem) {
        delay(1000)
        Timber.d("Network: Item added/updated with uid ${item.uid}")
    }

    suspend fun deleteItem(uid: String) {
        delay(1000)
        Timber.d("Network: Item deleted with uid $uid")
    }
}