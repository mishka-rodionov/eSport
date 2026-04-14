package com.rodionov.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Миграция с версии 27 на 28.
 * Добавляет поле startIntervalSeconds в таблицу orienteering_competitions.
 */
val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE orienteering_competitions ADD COLUMN startIntervalSeconds INTEGER"
        )
    }
}
