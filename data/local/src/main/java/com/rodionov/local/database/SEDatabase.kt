package com.rodionov.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.dao.UserDao
import com.rodionov.local.entities.CompetitionEntity

private const val DB_VERSION = 1

@Database(
    entities = [CompetitionEntity::class],
    version = DB_VERSION,
    exportSchema = true
)
@TypeConverters(CompetitionConverters::class)
abstract class SEDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}