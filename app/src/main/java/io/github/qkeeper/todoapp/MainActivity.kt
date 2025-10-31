package io.github.qkeeper.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.qkeeper.todoapp.ui.theme.TodoAppTheme
import timber.log.Timber
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    private val fileStorage by lazy { FileStorage(this) }
    private val viewModel: TodoViewModel by viewModels { TodoViewModelFactory(fileStorage) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoApp(viewModel)
            }
        }
    }
}

@Composable
private fun TodoApp(viewModel: TodoViewModel) {
    val navController = rememberNavController()
    val todoItems by viewModel.todoItems.collectAsState()

    NavHost(navController = navController, startDestination = AppDestinations.TODO_LIST) {
        composable(AppDestinations.TODO_LIST) {
            TodoListScreen(
                todos = todoItems,
                onAddNewTodo = {
                    navController.navigate(AppDestinations.EDIT_TODO)
                },
                onTodoClick = { todoId ->
                    navController.navigate("${AppDestinations.EDIT_TODO}?${AppDestinations.TODO_ID_ARG}=${todoId}")
                },
                onDelete = { todoId ->
                    viewModel.deleteTodoItem(todoId)
                }
            )
        }

        composable(
            route = AppDestinations.EDIT_TODO_WITH_ARG,
            arguments = listOf(navArgument(AppDestinations.TODO_ID_ARG) {
                type = NavType.StringType
                nullable = true
            })
        ) { backStateEntry ->
            val todoId = backStateEntry.arguments?.getString(AppDestinations.TODO_ID_ARG)
            val todoItem = todoId?.let { id ->
                viewModel.getTodoItem(id)
            } ?: TodoItem()

            EditTodoScreen(
                item = todoItem,
                isNew = todoId == null,
                onSave = { updatedItem ->
                    viewModel.addOrUpdate(updatedItem)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDelete = {
                    viewModel.deleteTodoItem(todoItem.uid)
                    navController.popBackStack()
                }
            )
        }
    }
}