package io.github.qkeeper.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.qkeeper.todoapp.Importance
import io.github.qkeeper.todoapp.TodoItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val importance: String,
    val deadline: Long?,
    val isDone: Boolean,
    val color: Int,
    val createdAt: Long,
    val changedAt: Long,
    // Поле, для демонстрации миграции (Усложнение)
    val lastUpdatedBy: String = "unknown"
) {
    fun toDomain(): TodoItem {
        val safeColor = if (color == 0) Color.White else Color(color)

        return TodoItem(
            uid = id,
            text = text,
            importance = Importance.valueOf(importance),
            deadline = deadline?.let {
                LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
            },
            isDone = isDone,
            color = safeColor
        )
    }

    companion object {
        fun fromDomain(item: TodoItem): TodoItemEntity {
            val now = Instant.now().epochSecond
            return TodoItemEntity(
                id = item.uid,
                text = item.text,
                importance = item.importance.name,
                deadline = item.deadline?.toEpochSecond(ZoneOffset.UTC),
                isDone = item.isDone,
                color = item.color.toArgb(),
                createdAt = now,
                changedAt = now,
                lastUpdatedBy = "me"
            )
        }
    }
}