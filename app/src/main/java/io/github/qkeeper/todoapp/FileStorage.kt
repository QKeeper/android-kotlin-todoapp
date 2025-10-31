package io.github.qkeeper.todoapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import java.io.IOException

class FileStorage(private val context: Context) {
    private val fileName = "todolist.json"

    fun save(items: List<TodoItem>) {
        val jsonArray = JSONArray()
        items.forEach { item -> jsonArray.put(item.json) }
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(jsonArray.toString().toByteArray())
            }
        } catch (e: IOException) {
            Timber.d(e.printStackTrace().toString())
        }
    }

    fun load(): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        try {
            val jsonString = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                TodoItem.parse(jsonObject)?.let {
                    items.add(it)
                }
            }
        } catch (e: IOException) {
            return emptyList()
        } catch (e: JSONException) {
            Timber.d(e.printStackTrace().toString())
            return emptyList()
        }
        return items
    }
}