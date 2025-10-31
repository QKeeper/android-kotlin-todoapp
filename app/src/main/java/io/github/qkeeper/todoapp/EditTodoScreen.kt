package io.github.qkeeper.todoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.qkeeper.todoapp.ui.theme.TodoAppTheme
import java.time.LocalDateTime
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.draw.clip
import timber.log.Timber

@Composable
fun EditTodoScreen(
    item: TodoItem,
    onSave: (TodoItem) -> Unit,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(item.text) }
    var importance by remember { mutableStateOf(item.importance) }
    var deadline by remember { mutableStateOf(item.deadline) }
    var color by remember { mutableStateOf(item.color) }

    val isSaveEnabled = text.isNotBlank()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EditTodoTopAppBar(
                isSaveEnabled = isSaveEnabled,
                onNavigateBack = onNavigateBack,
                onSave = {
                    val updatedItem = item.copy(
                        text = text,
                        importance = importance,
                        deadline = deadline
                    )
                    onSave(updatedItem)
                }
            )
        }
    ) {
        innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(scrollState),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                    },
                    placeholder = { Text("Что нужно сделать...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.colors(
                        // Убираем фон у самого поля ввода, т.к. фон задает Card
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        // Убираем линию-индикатор
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                ImportanceSelector(
                    importance = importance,
                    onImportanceChange = { newImportance ->
                        importance = newImportance
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DeadlineSelector(
                    deadline = deadline,
                    onDeadlineChange = { newDeadline ->
                        deadline = newDeadline
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ColorSelector(
                    selectedColor = color,
                    onColorChange = { newColor ->
                        color = newColor
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // <-- Добавим еще один разделитель

                DeleteButton(onDelete = onDelete)
            }
        }
    }
}

@Composable
fun ImportanceSelector(
    importance: Importance,
    onImportanceChange: (Importance) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Используем Box, чтобы DropdownMenu позиционировалось относительно него
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isMenuExpanded = true }
            .padding(12.dp) // Добавим отступы, чтобы было похоже на другие строки
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Важность",
                // Weight заставит этот Text занять все доступное место,
                // отодвинув выпадающий список вправо
                modifier = Modifier.weight(1f)
            )
            Text(
                text = when (importance) {
                    Importance.LOW -> "Низкая"
                    Importance.DEFAULT -> "Обычная"
                    Importance.HIGH -> "Высокая"
                },
            )
        }

        // Выпадающее меню теперь привязано к Box
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            modifier = Modifier.fillMaxWidth(0.5f) // Меню будет занимать половину ширины
        ) {
            DropdownMenuItem(
                text = { Text("Низкая") },
                onClick = {
                    onImportanceChange(Importance.LOW)
                    isMenuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Обычная") },
                onClick = {
                    onImportanceChange(Importance.DEFAULT)
                    isMenuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Высокая") },
                onClick = {
                    onImportanceChange(Importance.HIGH)
                    isMenuExpanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineSelector(
    deadline: LocalDateTime?,
    onDeadlineChange: (LocalDateTime?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Если нужно показать календарь, делаем это
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            // Устанавливаем в календаре текущую выбранную дату, если она есть
            initialSelectedDateMillis = deadline?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(millis),
                            ZoneId.systemDefault()
                        )
                        onDeadlineChange(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Сделать до")
            // Если дедлайн задан, показываем его
            deadline?.let {
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                Text(
                    text = it.format(formatter),
                    color = MaterialTheme.colorScheme.primary, // Выделяем дату цветом
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            }
        }
        Switch(
            checked = deadline != null,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    // Если включили, но даты еще нет - показываем календарь
                    // Если дата уже была, просто оставляем ее
                    if (deadline == null) {
                        showDatePicker = true
                    }
                } else {
                    // Если выключили - сбрасываем дедлайн
                    onDeadlineChange(null)
                }
            }
        )
    }
}

@Composable
fun DeleteButton(
    onDelete: () -> Unit,
) {
    TextButton(
        onClick = onDelete,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error // Красный цвет для опасных действий
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Удалить",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoTopAppBar(
    isSaveEnabled: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { /* Мы оставляем заголовок пустым */ },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Отменить"
                )
            }
        },
        actions = {
            TextButton(
                onClick = onSave,
                enabled = isSaveEnabled
            ) {
                Text("СОХРАНИТЬ")
            }
        }
    )
}

@Composable
fun ColorSelector(
    selectedColor: Color,
    onColorChange: (Color) -> Unit
) {
    val predefinedColors = listOf(
        Color(0xFFFF7675), // Красный
        Color(0xFFFAB1A0), // Оранжевый
        Color(0xFFFFEAA7), // Желтый
        Color(0xFF55EFC4), // Зеленый
        Color(0xFF81ECEC)  // Синий
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        predefinedColors.forEach { color ->
            ColorBox(
                color = color,
                isSelected = selectedColor == color,
                onClick = { onColorChange(color) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun ColorBox(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .height(40.dp)
        .padding(horizontal = 2.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(color)
        .clickable(onClick = onClick)
        .then(
            if (isSelected) Modifier.border(
                2.dp,
                MaterialTheme.colorScheme.onSurface,
                RoundedCornerShape(8.dp)
            ) else Modifier
        ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Цвет выбран",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditTodoScreenPreview() {
    TodoAppTheme {
        val sampleTodo = TodoItem(
            text = "Lorem ipsum dolor sit amet",
            importance = Importance.HIGH,
            deadline = LocalDateTime.now().plusDays(3)
        )
        EditTodoScreen(
            item = sampleTodo,
            onSave = {},
            onNavigateBack = {},
            onDelete = {}
        )
    }
}