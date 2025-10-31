package io.github.qkeeper.todoapp

object AppDestinations {
    const val TODO_LIST = "todo_list"
    const val EDIT_TODO = "edit_todo"
    const val TODO_ID_ARG = "todoId"
    val EDIT_TODO_WITH_ARG = "$EDIT_TODO?$TODO_ID_ARG={$TODO_ID_ARG}"
}