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

/**
 * Миграция с версии 32 на 33.
 * Добавляет поле imageUrl в таблицу orienteering_competitions.
 */
val MIGRATION_32_33 = object : Migration(32, 33) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE orienteering_competitions ADD COLUMN imageUrl TEXT"
        )
    }
}
