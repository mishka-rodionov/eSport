package com.rodionov.remote.di

import com.rodionov.domain.repository.OrienteeringCompetitionRepository
import com.rodionov.remote.datasource.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.repository.OrienteeringCompetitionRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orienteeringModule = module {
    singleOf(::OrienteeringCompetitionRemoteDataSource)
    singleOf(::OrienteeringCompetitionRemoteRepositoryImpl) bind OrienteeringCompetitionRepository::class
}