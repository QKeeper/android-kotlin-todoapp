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
import io.github.qkeeper.todoapp.ui.theme.TodoAppTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    // Используем `lazy`, чтобы экземпляр был создан только при первом обращении к нему.
    private val fileStorage by lazy { FileStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        Timber.d("Custom Application class initialized!")

        enableEdgeToEdge()
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
    }
}

@Composable
fun TodoList(
    modifier: Modifier = Modifier,
    items: List<TodoItem>,
    onAddItem: () -> Unit
) {

}