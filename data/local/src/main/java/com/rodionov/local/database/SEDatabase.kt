package com.rodionov.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodionov.domain.models.user.User
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.converters.UserConverter
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.UserDao
import com.rodionov.local.dao.orienteering.OrienteeringParticipantDao
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.OrienteeringParticipantEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity
import com.rodionov.local.entities.user.UserEntity

private const val DB_VERSION = 3

@Database(
    entities = [
        OrienteeringCompetitionEntity::class,
        ParticipantGroupEntity::class,
        OrienteeringParticipantEntity::class,
        UserEntity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(CompetitionConverters::class, UserConverter::class)
abstract class SEDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orienteeringCompetitionDao(): OrienteeringCompetitionDao
    abstract fun participantGroupsDao(): ParticipantGroupDao

    abstract fun orienteeringParticipantDao(): OrienteeringParticipantDao
}