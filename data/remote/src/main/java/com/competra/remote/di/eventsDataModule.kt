package com.competra.remote.di

import com.competra.domain.repository.events.CyclicEventDetailsRepository
import com.competra.domain.repository.events.EventsRepository
import com.competra.remote.datasource.events.CyclicEventDetailsRemoteDataSource
import com.competra.remote.datasource.events.EventsRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.events.CyclicEventDetailsRepositoryImpl
import com.competra.remote.repository.events.EventsRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val eventsDataModule = module {
    singleRemoteDataSourceOf(EventsRemoteDataSource::class.java)
    singleRemoteDataSourceOf(CyclicEventDetailsRemoteDataSource::class.java)
    singleOf(::EventsRepositoryImpl) bind EventsRepository::class
    factoryOf(::CyclicEventDetailsRepositoryImpl) bind CyclicEventDetailsRepository::class
}