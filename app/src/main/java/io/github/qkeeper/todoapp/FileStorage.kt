package io.github.qkeeper.todoapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import java.io.IOException

class FileStorage(private val context: Context) {
    private val fileName = "todolist.json"
    private val _todoItems = mutableListOf<TodoItem>()

    val todoItems: List<TodoItem>
        get() = _todoItems;

    fun addTodoItem(todo: TodoItem) {
        _todoItems.add(todo);
        Timber.d("Добавлена задача '${todo.uid}'")
    }

    fun deleteTodoItem(uid: String) {
        val itemToRemove = _todoItems.find { it.uid == uid }
        if (itemToRemove != null) {
            _todoItems.remove(itemToRemove)
            Timber.d("Удалена задача '${itemToRemove.uid}'")

        } else Timber.d("Задача '${uid}' не найдена для удаления")
    }

    fun updateTodoItem(updatedItem: TodoItem) {
        val index = _todoItems.indexOfFirst { it.uid == updatedItem.uid }
        if (index != -1) {
            _todoItems[index] = updatedItem;
            Timber.d("Задача '${updatedItem.uid}' изменена")
        } else Timber.d("Задача '${updatedItem.uid}' не найдена для обновления")
    }

    fun addOrUpdate(todoItem: TodoItem) {
        val index = _todoItems.indexOfFirst { it.uid == todoItem.uid }

        if (index != -1) {
            _todoItems[index] = todoItem;
            // Timber.d("Задача '${todoItem.uid}' обновлена")
            Timber.d("TODO JSON: ${todoItem.json}")
        } else {
            _todoItems.add(todoItem);
            Timber.d("Добавлена новая задача '${todoItem.uid}'")
        }
    }

    fun getTodoItem(uid: String): TodoItem? {
        return _todoItems.find { it.uid == uid }
    }

    fun save() {
        val jsonArray = JSONArray()
        todoItems.forEach { item -> jsonArray.put(item.json) }
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(jsonArray.toString().toByteArray())
            }
        } catch (e: IOException) {
            Timber.d( e.printStackTrace().toString())
        }
    }

    fun load(): List<TodoItem> {
        _todoItems.clear()
        try {
            val jsonString = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                TodoItem.parse(jsonObject)?.let {
                    _todoItems.add(it);
                }
            }

        } catch (e: IOException) {
            return emptyList()
        } catch (e: JSONException) {
            Timber.d(e.printStackTrace().toString())
            return emptyList()
        }
        return _todoItems;
    }
}