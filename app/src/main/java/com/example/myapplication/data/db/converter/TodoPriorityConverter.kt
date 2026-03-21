package com.example.myapplication.data.db.converter

import androidx.room.TypeConverter
import com.example.myapplication.domain.model.TodoPriority

class TodoPriorityConverter {
    @TypeConverter
    fun fromPriority(priority: TodoPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(name: String): TodoPriority {
        return try {
            TodoPriority.valueOf(name)
        } catch (e: IllegalArgumentException) {
            TodoPriority.LOW
        }
    }
}
