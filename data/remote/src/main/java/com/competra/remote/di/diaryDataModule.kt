package com.competra.remote.di

import com.competra.domain.repository.diary.WorkoutRemoteRepository
import com.competra.remote.datasource.diary.WorkoutRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.diary.WorkoutRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryDataModule = module {
    singleRemoteDataSourceOf(WorkoutRemoteDataSource::class.java)
    singleOf(::WorkoutRemoteRepositoryImpl) bind WorkoutRemoteRepository::class
}
