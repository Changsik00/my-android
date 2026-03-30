package com.example.myapplication.di

import com.example.myapplication.data.remote.source.WeatherRemoteDataSource
import com.example.myapplication.data.remote.source.WeatherRemoteDataSourceImpl
import com.example.myapplication.data.repository.TodoRepositoryImpl
import com.example.myapplication.data.repository.WeatherRepositoryImpl
import com.example.myapplication.domain.repository.TodoRepository
import com.example.myapplication.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl
    ): TodoRepository
    @Binds
    @Singleton
    abstract fun bindWeatherRemoteDataSource(
        weatherRemoteDataSourceImpl: WeatherRemoteDataSourceImpl
    ): WeatherRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
