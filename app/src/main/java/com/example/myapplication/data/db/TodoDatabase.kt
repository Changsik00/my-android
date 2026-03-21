package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.db.converter.LocalDateConverter
import com.example.myapplication.data.db.converter.TodoPriorityConverter

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, TodoPriorityConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
