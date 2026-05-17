package com.competra.remote.di

import com.competra.domain.repository.UploadRepository
import com.competra.remote.datasource.upload.UploadRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.UploadRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val uploadModule = module {
    singleRemoteDataSourceOf(UploadRemoteDataSource::class.java)
    singleOf(::UploadRepositoryImpl) bind UploadRepository::class
}
