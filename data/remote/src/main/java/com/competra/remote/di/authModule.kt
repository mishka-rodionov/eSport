package com.competra.remote.di

import com.competra.domain.repository.auth.AuthRepository
import com.competra.domain.repository.user.UserProfileRepository
import com.competra.remote.datasource.auth.AuthRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.auth.AuthRepositoryImpl
import com.competra.remote.repository.user.UserProfileRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleRemoteDataSourceOf(AuthRemoteDataSource::class.java)
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::UserProfileRepositoryImpl) bind UserProfileRepository::class
}