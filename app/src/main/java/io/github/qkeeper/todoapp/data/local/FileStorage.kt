package io.github.qkeeper.todoapp.data.local

import android.content.Context
import io.github.qkeeper.todoapp.TodoItem
import io.github.qkeeper.todoapp.json
import io.github.qkeeper.todoapp.parse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber
import java.io.IOException

class FileStorage(private val context: Context) {
    private val fileName = "todolist.json"
    private val mutex = Mutex()

    suspend fun save(items: List<TodoItem>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val jsonArray = JSONArray()
            items.forEach { item -> jsonArray.put(item.json) }
            try {
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                    outputStream.write(jsonArray.toString().toByteArray())
                }
            } catch (e: IOException) {
                Timber.e(e, "Error saving todo list")
                throw e
            }
        }
    }

    suspend fun load(): List<TodoItem> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val items = mutableListOf<TodoItem>()
            try {
                if (!context.getFileStreamPath(fileName).exists()) {
                    return@withLock emptyList()
                }
                val jsonString = context.openFileInput(fileName).bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    TodoItem.parse(jsonObject)?.let {
                        items.add(it)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading todo list")
                return@withLock emptyList()
            }
            return@withLock items
        }
    }
}