package com.rodionov.remote.di

import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.domain.repository.user.UserProfileRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.extension.singleRemoteDataSourceOf
import com.rodionov.remote.repository.auth.AuthRepositoryImpl
import com.rodionov.remote.repository.user.UserProfileRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleRemoteDataSourceOf(AuthRemoteDataSource::class.java)
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::UserProfileRepositoryImpl) bind UserProfileRepository::class
}