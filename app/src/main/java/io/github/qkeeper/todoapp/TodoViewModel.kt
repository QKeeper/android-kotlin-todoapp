// TodoViewModel.kt
package io.github.qkeeper.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.qkeeper.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class TodoViewModel(
    private val repository: TodoItemsRepository
) : ViewModel() {
    val todoItems: StateFlow<List<TodoItem>> = repository.itemsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "ViewModel Coroutine Error")
        viewModelScope.launch {
            _errorEvents.emit("Ошибка: ${throwable.localizedMessage}")
        }
    }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(exceptionHandler) {
            repository.refreshData()
        }
    }

    fun deleteTodoItem(uid: String) {
        viewModelScope.launch(exceptionHandler) {
            repository.deleteItem(uid)
        }
    }

    fun addOrUpdate(todoItem: TodoItem) {
        viewModelScope.launch(exceptionHandler) {
            repository.addItem(todoItem)
        }
    }

    fun getTodoItem(uid: String): TodoItem? {
        return todoItems.value.find { it.uid == uid }
    }

    fun updateTodo(item: TodoItem) {
        viewModelScope.launch(exceptionHandler) {
            repository.updateItem(item)
        }
    }
}

class TodoViewModelFactory(
    private val repository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}