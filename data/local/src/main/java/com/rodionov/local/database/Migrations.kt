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

/**
 * Миграция с версии 36 на 37.
 * Доводит все синхронизируемые сущности до общего «sync trait»:
 * добавляет serverUpdatedAt и syncError всем, isDeleted/lastModified участникам и результатам,
 * remoteId результатам. Также добавляет индексы по isSynced для быстрой выборки несинхронизированных.
 *
 * Поле serverUpdatedAt в Competition (Embedded) и в orienteering_competitions добавляется
 * без префикса, т.к. Competition.kt не использует prefix в @Embedded.
 */
val MIGRATION_36_37 = object : Migration(36, 37) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Competition (@Embedded в orienteering_competitions): serverUpdatedAt + syncError.
        // syncError было в data class Competition давно, но миграция для него не существовала —
        // добиваем ALTER TABLE сейчас. Если поле уже есть (на новых установках через Room
        // generated schema) — ловим SQLException и продолжаем.
        db.execSQL("ALTER TABLE orienteering_competitions ADD COLUMN serverUpdatedAt INTEGER")
        runCatching {
            db.execSQL("ALTER TABLE orienteering_competitions ADD COLUMN syncError TEXT")
        }

        // participant_groups
        db.execSQL("ALTER TABLE participant_groups ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE participant_groups ADD COLUMN syncError TEXT")

        // distances
        db.execSQL("ALTER TABLE distances ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE distances ADD COLUMN syncError TEXT")

        // organizers
        db.execSQL("ALTER TABLE organizers ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE organizers ADD COLUMN syncError TEXT")

        // stages
        db.execSQL("ALTER TABLE stages ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE stages ADD COLUMN syncError TEXT")

        // orienteering_participants — добавляем sync-поля
        db.execSQL("ALTER TABLE orienteering_participants ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE orienteering_participants ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE orienteering_participants ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE orienteering_participants ADD COLUMN syncError TEXT")

        // orienteering_results — добавляем sync-поля + remoteId
        db.execSQL("ALTER TABLE orienteering_results ADD COLUMN remoteId TEXT")
        db.execSQL("ALTER TABLE orienteering_results ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE orienteering_results ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE orienteering_results ADD COLUMN serverUpdatedAt INTEGER")
        db.execSQL("ALTER TABLE orienteering_results ADD COLUMN syncError TEXT")

        // Индексы под быструю выборку незасинканных
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_competitions_unsynced ON orienteering_competitions(isSynced)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_groups_unsynced ON participant_groups(isSynced)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_distances_unsynced ON distances(isSynced)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_participants_unsynced ON orienteering_participants(isSynced)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_results_unsynced ON orienteering_results(isSynced)")
    }
}
