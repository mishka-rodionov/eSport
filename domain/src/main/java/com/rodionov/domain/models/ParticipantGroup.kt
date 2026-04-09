package com.rodionov.domain.models

import com.rodionov.domain.models.orienteering.ControlPoint

data class ParticipantGroup(
    val groupId: Long,
    val competitionId: Long,
    val title: String,
    val gender: Gender?,                   // MALE, FEMALE, MIXED, null – не задано
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val distanceId: Long,                  // ссылка на дистанцию
    val maxParticipants: Int? = null,      // лимит для группы
    // Поля синхронизации
    val remoteId: String? = null,          // UUID группы на сервере (null = не синхронизирована)
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)