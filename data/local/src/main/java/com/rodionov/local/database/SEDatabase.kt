package com.rodionov.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodionov.domain.models.user.User
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.converters.ControlPointConverters
import com.rodionov.local.converters.ResultConverters
import com.rodionov.local.converters.UserConverter
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.UserDao
import com.rodionov.local.dao.orienteering.OrienteeringParticipantDao
import com.rodionov.local.dao.orienteering.OrienteeringResultDao
import com.rodionov.local.entities.orienteering.DistanceEntity
import com.rodionov.local.entities.orienteering.OrganizerEntity
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.OrienteeringParticipantEntity
import com.rodionov.local.entities.orienteering.OrienteeringResultEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity
import com.rodionov.local.entities.orienteering.StageEntity
import com.rodionov.local.entities.user.UserEntity

private const val DB_VERSION = 19

@Database(
    entities = [
        OrienteeringCompetitionEntity::class,
        ParticipantGroupEntity::class,
        OrienteeringParticipantEntity::class,
        UserEntity::class,
        OrienteeringResultEntity::class,
        DistanceEntity::class,
        OrganizerEntity::class,
        StageEntity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(
    CompetitionConverters::class,
    UserConverter::class,
    ControlPointConverters::class,
    ResultConverters::class
)
abstract class SEDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orienteeringCompetitionDao(): OrienteeringCompetitionDao
    abstract fun participantGroupsDao(): ParticipantGroupDao

    abstract fun orienteeringParticipantDao(): OrienteeringParticipantDao
    abstract fun orienteeringResultDao(): OrienteeringResultDao

}