package io.github.qkeeper.todoapp

import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

val TodoItem.json: JSONObject
    get() {
        val json = JSONObject();

        json.put("uid", uid)
        json.put("text", text)
        if (importance != Importance.DEFAULT)
            json.put("importance", importance.name)
        if (color != 0xFFFFFFFF)
            json.put("color", color)
        deadline?.let {
            json.put("deadline", it.toEpochSecond(ZoneOffset.UTC))
        }
        json.put("isDone", isDone)

        return json;
    }

fun TodoItem.Companion.parse(json: JSONObject): TodoItem? {
    return try {
        val uid = json.getString("uid")
        val text = json.getString("text")
        val importance = Importance.valueOf(json.optString("importance", Importance.DEFAULT.name))
        val color = json.optLong("color", 0xFFFFFFFF)
        val deadline = if (json.has("deadline"))
            LocalDateTime.ofInstant(Instant.ofEpochSecond(json.getLong("deadline")), ZoneOffset.UTC)
        else null
        val isDone = json.getBoolean("isDone")

        TodoItem(uid, text, importance, color, deadline, isDone)
    } catch (e: Exception) {
        null
    }
}