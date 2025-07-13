package com.rodionov.local.database

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME = "sport-enthusiast-db"

class DatabaseHelper(private val context: Context) {

    val db: SEDatabase by lazy { createDatabase() }

    fun createDatabase() =
        Room
            .databaseBuilder(context = context, SEDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

}