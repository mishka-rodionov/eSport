package com.competra.remote.di

import com.competra.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.competra.remote.datasource.orienteering.OrienteeringCompetitionRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.orienteering.OrienteeringCompetitionRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orienteeringModule = module {
    singleRemoteDataSourceOf(OrienteeringCompetitionRemoteDataSource::class.java)
    singleOf(::OrienteeringCompetitionRemoteRepositoryImpl) bind OrienteeringCompetitionRemoteRepository::class
}