package io.github.qkeeper.todoapp.data

import io.github.qkeeper.todoapp.TodoItem
import io.github.qkeeper.todoapp.data.local.FileStorage
import io.github.qkeeper.todoapp.data.remote.NetworkDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class TodoItemsRepository(
    private val fileStorage: FileStorage,
    private val networkDataSource: NetworkDataSource
) {
    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    val itemsFlow: StateFlow<List<TodoItem>> = _itemsFlow.asStateFlow()

    suspend fun refreshData() {
        val localItems = fileStorage.load()
        _itemsFlow.value = localItems

        try {
            val remoteItems = networkDataSource.loadItems()
            if (remoteItems.isNotEmpty()) {
                _itemsFlow.value = remoteItems
                fileStorage.save(remoteItems)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync with network")
            throw e
        }
    }

    suspend fun addItem(item: TodoItem) {
        val currentList = _itemsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == item.uid }
        if (index != -1) {
            currentList[index] = item
        } else {
            currentList.add(item)
        }

        _itemsFlow.update { currentList }
        fileStorage.save(currentList)
        networkDataSource.addItem(item)
    }

    suspend fun deleteItem(uid: String) {
        val currentList = _itemsFlow.value.filter { it.uid != uid }

        _itemsFlow.value = currentList
        fileStorage.save(currentList)
        networkDataSource.deleteItem(uid)
    }

    fun getItem(uid: String): TodoItem? {
        return _itemsFlow.value.find { it.uid == uid }
    }
}