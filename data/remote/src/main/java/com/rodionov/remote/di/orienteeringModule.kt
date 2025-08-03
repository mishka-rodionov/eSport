package com.rodionov.remote.di

import com.rodionov.domain.repository.OrienteeringCompetitionRemoteRepository
import com.rodionov.remote.datasource.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.extension.singleRemoteDataSourceOf
import com.rodionov.remote.repository.OrienteeringCompetitionRemoteRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orienteeringModule = module {
    singleRemoteDataSourceOf(OrienteeringCompetitionRemoteDataSource::class.java)
    singleOf(::OrienteeringCompetitionRemoteRemoteRepositoryImpl) bind OrienteeringCompetitionRemoteRepository::class
}