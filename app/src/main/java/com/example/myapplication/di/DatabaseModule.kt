package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.db.MIGRATION_1_2
import com.example.myapplication.data.db.TodoDao
import com.example.myapplication.data.db.TodoDatabase
import com.example.myapplication.data.db.TodoSummaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(
        @ApplicationContext context: Context
    ): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_scheduler.db"
        )
            .addMigrations(MIGRATION_1_2) // SPEC-605: v1→v2 Migration 등록
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    // SPEC-605: TodoSummaryDao 제공
    @Provides
    @Singleton
    fun provideTodoSummaryDao(database: TodoDatabase): TodoSummaryDao {
        return database.todoSummaryDao()
    }
}
