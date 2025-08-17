package com.rodionov.remote.di

import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.remote.datasource.events.EventsRemoteDataSource
import com.rodionov.remote.extension.singleRemoteDataSourceOf
import com.rodionov.remote.repository.events.EventsRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val eventsDataModule = module {
    singleRemoteDataSourceOf(EventsRemoteDataSource::class.java)
    singleOf(::EventsRepositoryImpl) bind EventsRepository::class
}