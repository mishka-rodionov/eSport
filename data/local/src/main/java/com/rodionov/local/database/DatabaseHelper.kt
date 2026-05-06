package com.rodionov.local.database

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME = "sport-enthusiast-db"

class DatabaseHelper(private val context: Context) {

    val db: SEDatabase by lazy { createDatabase() }

    fun createDatabase() =
        Room
            .databaseBuilder(context = context, SEDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_27_28, MIGRATION_32_33, MIGRATION_33_34, MIGRATION_34_35, MIGRATION_35_36, MIGRATION_36_37)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

}