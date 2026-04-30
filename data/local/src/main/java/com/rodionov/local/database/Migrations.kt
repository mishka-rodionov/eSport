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

/**
 * Миграция с версии 35 на 36.
 * Переименовывает колонку photo → avatarUrl в таблице users.
 */
val MIGRATION_35_36 = object : Migration(35, 36) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE users_new (
                id TEXT NOT NULL,
                firstName TEXT NOT NULL,
                lastName TEXT NOT NULL,
                middleName TEXT,
                birthDate INTEGER NOT NULL,
                gender TEXT NOT NULL,
                avatarUrl TEXT NOT NULL DEFAULT '',
                phoneNumber TEXT,
                email TEXT NOT NULL,
                qualification TEXT NOT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent())
        db.execSQL("INSERT INTO users_new SELECT id, firstName, lastName, middleName, birthDate, gender, photo, phoneNumber, email, qualification FROM users")
        db.execSQL("DROP TABLE users")
        db.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}

/**
 * Миграция с версии 34 на 35.
 * Добавляет поле isDrawConducted в таблицу orienteering_competitions.
 */
val MIGRATION_34_35 = object : Migration(34, 35) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE orienteering_competitions ADD COLUMN isDrawConducted INTEGER NOT NULL DEFAULT 0"
        )
    }
}

/**
 * Миграция с версии 33 на 34.
 * Меняет тип participantId в orienteering_results с INTEGER на TEXT,
 * чтобы соответствовать String-типу PK в orienteering_participants.
 */
val MIGRATION_33_34 = object : Migration(33, 34) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS orienteering_results_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                competitionId INTEGER NOT NULL,
                groupId INTEGER NOT NULL,
                participantId TEXT NOT NULL,
                startTime INTEGER,
                finishTime INTEGER,
                totalTime INTEGER,
                rank INTEGER,
                status TEXT NOT NULL,
                penaltyTime INTEGER NOT NULL DEFAULT 0,
                splits TEXT,
                isEditable INTEGER NOT NULL DEFAULT 1,
                isEdited INTEGER NOT NULL DEFAULT 0,
                isSynced INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(competitionId) REFERENCES orienteering_competitions(localCompetitionId) ON DELETE CASCADE,
                FOREIGN KEY(participantId) REFERENCES orienteering_participants(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO orienteering_results_new
            SELECT id, competitionId, groupId, CAST(participantId AS TEXT),
                   startTime, finishTime, totalTime, rank, status,
                   penaltyTime, splits, isEditable, isEdited, isSynced
            FROM orienteering_results
        """.trimIndent())
        db.execSQL("DROP TABLE orienteering_results")
        db.execSQL("ALTER TABLE orienteering_results_new RENAME TO orienteering_results")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_orienteering_results_competitionId ON orienteering_results(competitionId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_orienteering_results_participantId ON orienteering_results(participantId)")
    }
}
