package com.rodionov.remote.di

import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.remote.datasource.orienteering.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.extension.singleRemoteDataSourceOf
import com.rodionov.remote.repository.orienteering.OrienteeringCompetitionRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orienteeringModule = module {
    singleRemoteDataSourceOf(OrienteeringCompetitionRemoteDataSource::class.java)
    singleOf(::OrienteeringCompetitionRemoteRepositoryImpl) bind OrienteeringCompetitionRemoteRepository::class
}