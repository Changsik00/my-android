package com.example.myapplication.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.domain.model.TodoPriority
import java.time.LocalDate

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val priority: TodoPriority = TodoPriority.LOW
)
