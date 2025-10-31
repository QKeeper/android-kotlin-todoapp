package io.github.qkeeper.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.qkeeper.todoapp.AppDestinations
import io.github.qkeeper.todoapp.ui.theme.TodoAppTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    // Используем `lazy`, чтобы экземпляр был создан только при первом обращении к нему.
    private val fileStorage by lazy { FileStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileStorage.load()

        Timber.plant(Timber.DebugTree())
        Timber.d("Custom Application class initialized!")
        Timber.d("${fileStorage.todoItems[0]}")

        enableEdgeToEdge()
<<<<<<< Updated upstream
//        setContent {
//            TodoAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }

        // FileStorage test
        Timber.d("onCreate")
        fileStorage.load();
        fileStorage.addTodoItem(TodoItem(text = "Test item"));
        Timber.d(fileStorage.todoItems.toString());
        fileStorage.save();
=======
        setContent {
            TodoAppTheme {
                TodoApp(fileStorage)
            }
        }
    }
}

@Composable
private fun TodoApp(fileStorage: FileStorage) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.TODO_LIST) {
        composable(AppDestinations.TODO_LIST) {
            TodoListScreen(
                storage = fileStorage,
                onAddNewTodo = {
                    navController.navigate(AppDestinations.EDIT_TODO)
                },
                onTodoClick = { todoId ->
                    navController.navigate("${AppDestinations.EDIT_TODO}?${AppDestinations.TODO_ID_ARG}=${todoId}")
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
            val todoItem = todoId.let { id ->
                fileStorage.todoItems.find { it.uid == id }
            } ?: TodoItem()

            EditTodoScreen(
                item = todoItem,
                isNew = todoId == null,
                onSave = { updatedItem ->
                    fileStorage.addOrUpdate(updatedItem)
                    fileStorage.save()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDelete = {
                    fileStorage.deleteTodoItem(todoItem.uid)
                    fileStorage.save()
                    navController.popBackStack()
                }
            )
        }
>>>>>>> Stashed changes
    }
}

@Composable
fun TodoList(
    modifier: Modifier = Modifier,
    items: List<TodoItem>,
    onAddItem: () -> Unit
) {

}