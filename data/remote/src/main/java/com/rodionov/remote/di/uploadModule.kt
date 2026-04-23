package com.rodionov.remote.di

import com.rodionov.domain.repository.UploadRepository
import com.rodionov.remote.datasource.upload.UploadRemoteDataSource
import com.rodionov.remote.extension.singleRemoteDataSourceOf
import com.rodionov.remote.repository.UploadRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val uploadModule = module {
    singleRemoteDataSourceOf(UploadRemoteDataSource::class.java)
    singleOf(::UploadRepositoryImpl) bind UploadRepository::class
}
