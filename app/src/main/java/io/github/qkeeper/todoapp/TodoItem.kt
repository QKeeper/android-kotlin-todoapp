package io.github.qkeeper.todoapp

import java.time.LocalDateTime
import java.util.UUID

data class TodoItem(
    val uid: String = UUID.randomUUID().toString(),
    var text: String,
    var importance: Importance = Importance.DEFAULT,
    var color: Long = 0xFFFFFFFF,
    var deadline: LocalDateTime? = null,
    var isDone: Boolean = false,
) { companion object }

enum class Importance {
    LOW,
    DEFAULT,
    HIGH
}