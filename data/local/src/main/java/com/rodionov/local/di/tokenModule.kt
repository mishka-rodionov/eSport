package com.rodionov.local.di

import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.local.repository.auth.TokenRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val tokenModule = module {
    singleOf(::TokenRepositoryImpl) bind TokenRepository::class
}