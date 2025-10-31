package io.github.qkeeper.todoapp

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.util.UUID

data class TodoItem(
    val uid: String = UUID.randomUUID().toString(),
    var text: String = "",
    var importance: Importance = Importance.DEFAULT,
    var color: Color = Color.White,
    var deadline: LocalDateTime? = null,
    var isDone: Boolean = false,
) { companion object }

enum class Importance {
    LOW,
    DEFAULT,
    HIGH
}