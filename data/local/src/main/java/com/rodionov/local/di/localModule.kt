package com.rodionov.local.di

import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.local.repository.OrienteeringCompetitionLocalRepositoryImpl
import com.rodionov.local.repository.auth.TokenRepositoryImpl
import com.rodionov.local.repository.user.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localModule = module {
    singleOf(::TokenRepositoryImpl) bind TokenRepository::class
    singleOf(::OrienteeringCompetitionLocalRepositoryImpl) bind OrienteeringCompetitionLocalRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}