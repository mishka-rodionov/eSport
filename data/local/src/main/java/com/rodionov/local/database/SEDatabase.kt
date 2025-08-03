package com.rodionov.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.UserDao
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity

private const val DB_VERSION = 1

@Database(
    entities = [
        OrienteeringCompetitionEntity::class,
        ParticipantGroupEntity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(CompetitionConverters::class)
abstract class SEDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orienteeringCompetitionDao(): OrienteeringCompetitionDao
    abstract fun participantGroupsDao(): ParticipantGroupDao
}