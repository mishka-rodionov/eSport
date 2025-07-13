package com.rodionov.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodionov.local.UserDao

private const val DB_VERSION = 1

@Database(
    entities = [],
    version = DB_VERSION,
    exportSchema = true
)
abstract class SEDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}