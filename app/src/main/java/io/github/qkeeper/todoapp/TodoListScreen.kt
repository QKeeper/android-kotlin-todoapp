package io.github.qkeeper.todoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodoListScreen(
    storage: FileStorage,
    onAddNewTodo: () -> Unit,
    onTodoClick: (String) -> Unit
) {
    storage.load()
    val todos = storage.todoItems;

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewTodo) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить новую задачу")
            }
        }
    ) {
        innerPadding ->

        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            todos.forEach {
                    todoItem -> Text(
                        text = todoItem.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .background(todoItem.color)
                            .clickable { onTodoClick(todoItem.uid) }
                    )
            }
        }
    }
}