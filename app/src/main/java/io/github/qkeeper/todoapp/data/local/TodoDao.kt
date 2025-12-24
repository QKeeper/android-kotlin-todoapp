package io.github.qkeeper.todoapp.data.local

import androidx.room.*
import io.github.qkeeper.todoapp.data.local.entity.TodoItemEntity

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items")
    suspend fun getAll(): List<TodoItemEntity>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getById(id: String): TodoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(item: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(items: List<TodoItemEntity>)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM todo_items")
    suspend fun clear()
}