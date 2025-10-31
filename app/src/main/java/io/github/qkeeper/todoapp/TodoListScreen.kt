package io.github.qkeeper.todoapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val todos = storage.todoItems

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewTodo) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить новую задачу")
            }
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp) // Добавим отступы по бокам для всего списка
        ) {
            items(
                items = todos,
                key = { it.uid }
            ) { todoItem ->
                TodoListItem(
                    item = todoItem,
                    onClick = { onTodoClick(todoItem.uid) }
                )
            }
        }
    }
}

/**
 * Composable-функция для отображения одного элемента в списке дел.
 */
@Composable
fun TodoListItem(
    item: TodoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = item.color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = item.text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}