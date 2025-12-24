package io.github.qkeeper.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import io.github.qkeeper.todoapp.TodoItem
import io.github.qkeeper.todoapp.data.local.TodoDatabase
import io.github.qkeeper.todoapp.data.local.entity.TodoItemEntity
import io.github.qkeeper.todoapp.data.remote.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber

class TodoItemsRepository(
    context: Context,
    private val networkDataSource: NetworkDataSource
) {
    private val db = TodoDatabase.getDatabase(context)
    private val dao = db.todoDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    val itemsFlow: StateFlow<List<TodoItem>> = _itemsFlow.asStateFlow()

    private var revision: Int
        get() = prefs.getInt("revision", 0)
        set(value) = prefs.edit().putInt("revision", value).apply()

    suspend fun loadLocalData() {
        val entities = dao.getAll()
        _itemsFlow.value = entities.map { it.toDomain() }
    }

    suspend fun refreshData() {
        try {
            val (remoteItems, newRevision) = networkDataSource.loadItems()

            val uniqueItems = remoteItems.distinctBy { it.uid }

            withContext(Dispatchers.IO) {
                dao.clear()
                dao.saveAll(uniqueItems.map { TodoItemEntity.fromDomain(it) })
            }

            revision = newRevision
            _itemsFlow.value = uniqueItems
            Timber.d("Sync success. New revision: $revision")

        } catch (e: Exception) {
            Timber.e(e, "Failed to sync with network. Working offline.")
        }
    }

    suspend fun addItem(item: TodoItem) {
        val newEntity = TodoItemEntity.fromDomain(item)
        dao.save(newEntity)
        _itemsFlow.update { list -> list + item }

        try {
            val newRevision = networkDataSource.addItem(revision, item)
            revision = newRevision
        } catch (e: Exception) {
            Timber.e(e, "Failed to add item to server. Item saved locally.")
        }
    }

    suspend fun addOrUpdate(item: TodoItem) {
        val isInMemory = _itemsFlow.value.any { it.uid == item.uid }

        if (isInMemory) {
            updateItem(item)
        } else {
            val existingInDb = dao.getById(item.uid)
            if (existingInDb != null) {
                updateItem(item)
            } else {
                addItem(item)
            }
        }
    }

    suspend fun updateItem(item: TodoItem) {
        dao.save(TodoItemEntity.fromDomain(item))
        _itemsFlow.update { list -> list.map { if (it.uid == item.uid) item else it } }

        try {
            val newRevision = networkDataSource.updateItem(revision, item)
            revision = newRevision
        } catch (e: Exception) {
            Timber.e(e, "Failed to update item on server")
        }
    }

    suspend fun deleteItem(uid: String) {
        dao.delete(uid)
        _itemsFlow.update { list -> list.filter { it.uid != uid } }

        try {
            val newRevision = networkDataSource.deleteItem(revision, uid)
            revision = newRevision
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete item on server")
        }
    }

    suspend fun getItem(uid: String): TodoItem? {
        return dao.getById(uid)?.toDomain()
    }
}