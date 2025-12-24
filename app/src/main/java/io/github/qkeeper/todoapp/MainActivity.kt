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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.qkeeper.todoapp.data.TodoItemsRepository
import io.github.qkeeper.todoapp.data.remote.NetworkDataSource
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val networkDataSource by lazy { NetworkDataSource() }

    private val repository by lazy {
        TodoItemsRepository(applicationContext, networkDataSource)
    }

    private val viewModel: TodoViewModel by viewModels { TodoViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        lifecycleScope.launch {
            repository.loadLocalData()
        }

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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

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
                onTodoUpdate = { updatedItem ->
                    viewModel.updateTodo(updatedItem)
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
                    if (todoId != null) {
                        viewModel.updateTodo(updatedItem)
                    } else {
                        viewModel.addOrUpdate(updatedItem)
                    }
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