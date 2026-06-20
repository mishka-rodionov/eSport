package com.competra.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.competra.domain.models.orienteering.OrganizerRole
import com.competra.local.entities.user.UserEntity

/**
 * Таблица организаторов соревнования по ориентированию.
 * Связь many‑to‑many между пользователями и соревнованиями с указанием роли.
 * Если организатор не является зарегистрированным пользователем системы, можно сохранить имя и контакт.
 * */
@Entity(
    tableName = "organizers",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["competitionId"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,       // предполагается, что есть модель User
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("competitionId"), Index("userId")]
)
data class OrganizerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: Long? = null,
    val competitionId: String,
    val userId: Long? = null,               // если пользователь есть в системе
    val name: String? = null,               // если пользователь не зарегистрирован
    val role: OrganizerRole,                // MAIN, JUDGE, SECRETARY, etc.
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val serverUpdatedAt: Long? = null,
    val syncError: String? = null
)