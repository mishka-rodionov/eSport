package com.competra.remote.di

import com.competra.domain.repository.rating.RatingRepository
import com.competra.remote.datasource.rating.RatingRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.rating.RatingRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val ratingDataModule = module {
    singleRemoteDataSourceOf(RatingRemoteDataSource::class.java)
    singleOf(::RatingRepositoryImpl) bind RatingRepository::class
}
