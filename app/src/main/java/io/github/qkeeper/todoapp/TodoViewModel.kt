package io.github.qkeeper.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoViewModel(private val fileStorage: FileStorage) : ViewModel() {

    private val _todoItems = MutableStateFlow<List<TodoItem>>(emptyList())
    val todoItems = _todoItems.asStateFlow()

    init {
        _todoItems.value = fileStorage.load()
    }

    fun deleteTodoItem(uid: String) {
        val newList = _todoItems.value.filter { it.uid != uid }
        _todoItems.value = newList
        fileStorage.save(newList)
    }

    fun addOrUpdate(todoItem: TodoItem) {
        val currentList = _todoItems.value
        val index = currentList.indexOfFirst { it.uid == todoItem.uid }

        val newList = if (index != -1) {
            currentList.toMutableList().apply { this[index] = todoItem }
        } else {
            currentList + todoItem
        }

        _todoItems.value = newList
        fileStorage.save(newList)
    }

    fun getTodoItem(uid: String): TodoItem? {
        return _todoItems.value.find { it.uid == uid }
    }
}

class TodoViewModelFactory(private val fileStorage: FileStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(fileStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}