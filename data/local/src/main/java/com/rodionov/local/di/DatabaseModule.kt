package com.rodionov.local.di

import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.UserDao
import com.rodionov.local.database.DatabaseHelper
import com.rodionov.local.repository.OrienteeringCompetitionLocalRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    singleOf(::DatabaseHelper)
    single<UserDao> { get<DatabaseHelper>().db.userDao() }
    single<OrienteeringCompetitionDao> { get<DatabaseHelper>().db.orienteeringCompetitionDao() }
    single<ParticipantGroupDao> { get<DatabaseHelper>().db.participantGroupsDao() }
    singleOf(::OrienteeringCompetitionLocalRepositoryImpl) bind OrienteeringCompetitionLocalRepository::class
}