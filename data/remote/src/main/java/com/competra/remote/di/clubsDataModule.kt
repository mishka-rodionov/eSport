package com.competra.remote.di

import com.competra.domain.repository.clubs.ClubJoinRequestRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.domain.repository.clubs.TeamRepository
import com.competra.remote.datasource.clubs.ClubRemoteDataSource
import com.competra.remote.datasource.clubs.TeamRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import com.competra.remote.repository.clubs.ClubJoinRequestRepositoryImpl
import com.competra.remote.repository.clubs.ClubRepositoryImpl
import com.competra.remote.repository.clubs.TeamRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val clubsDataModule = module {
    singleRemoteDataSourceOf(ClubRemoteDataSource::class.java)
    singleRemoteDataSourceOf(TeamRemoteDataSource::class.java)
    singleOf(::ClubRepositoryImpl) bind ClubRepository::class
    singleOf(::TeamRepositoryImpl) bind TeamRepository::class
    singleOf(::ClubJoinRequestRepositoryImpl) bind ClubJoinRequestRepository::class
}
