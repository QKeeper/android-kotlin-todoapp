package io.github.qkeeper.todoapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import io.github.qkeeper.todoapp.Importance
import io.github.qkeeper.todoapp.TodoItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import android.graphics.Color as AndroidColor
import androidx.core.graphics.toColorInt
import timber.log.Timber

data class TodoListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("list") val list: List<TodoItemDto>,
    @SerializedName("revision") val revision: Int
)

data class TodoItemResponse(
    @SerializedName("status") val status: String,
    @SerializedName("element") val element: TodoItemDto,
    @SerializedName("revision") val revision: Int
)

data class TodoItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("done") val isDone: Boolean,
    @SerializedName("color") val color: String?,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("changed_at") val changedAt: Long,
    @SerializedName("last_updated_by") val lastUpdatedBy: String
) {
    fun toDomain(): TodoItem {
        val parsedColor = try {
            if (color != null && color.isNotBlank()) {
                val c = color.toColorInt()
                if (c == 0) Color.White else Color(c)
            } else {
                Color.White
            }
        } catch (e: Exception) {
            Color.White
        }

        return TodoItem(
            uid = id,
            text = text,
            importance = when (importance) {
                "low" -> Importance.LOW
                "important" -> Importance.HIGH
                else -> Importance.DEFAULT
            },
            deadline = deadline?.let {
                LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
            },
            isDone = isDone,
            color = parsedColor
        )
    }

    companion object {
        fun fromDomain(item: TodoItem): TodoItemDto {
            val now = Instant.now().epochSecond

            val colorHex = String.format(Locale.US, "#%08X", item.color.toArgb())

            Timber.d("APP -> SERVER: ID=${item.uid}, ColorHex='$colorHex'")

            return TodoItemDto(
                id = item.uid,
                text = item.text,
                importance = when (item.importance) {
                    Importance.LOW -> "low"
                    Importance.HIGH -> "important"
                    Importance.DEFAULT -> "basic"
                },
                deadline = item.deadline?.toEpochSecond(ZoneOffset.UTC),
                isDone = item.isDone,
                color = colorHex,
                createdAt = now,
                changedAt = now,
                lastUpdatedBy = "android_device"
            )
        }
    }
}