package com.rodionov.local.di

import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.UserDao
import com.rodionov.local.dao.orienteering.DistanceDao
import com.rodionov.local.dao.orienteering.OrienteeringParticipantDao
import com.rodionov.local.dao.orienteering.OrienteeringResultDao
import com.rodionov.local.database.DatabaseHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    singleOf(::DatabaseHelper)
    single<UserDao> { get<DatabaseHelper>().db.userDao() }
    single<OrienteeringCompetitionDao> { get<DatabaseHelper>().db.orienteeringCompetitionDao() }
    single<ParticipantGroupDao> { get<DatabaseHelper>().db.participantGroupsDao() }
    single<OrienteeringParticipantDao> { get<DatabaseHelper>().db.orienteeringParticipantDao() }
    single<OrienteeringResultDao> { get<DatabaseHelper>().db.orienteeringResultDao() }
    single<DistanceDao> { get<DatabaseHelper>().db.distanceDao() }
}