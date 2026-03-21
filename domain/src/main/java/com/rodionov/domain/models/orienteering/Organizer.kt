package com.rodionov.domain.models.orienteering

data class Organizer(
    val remoteId: Long? = null,
    val competitionId: Long,
    val userId: Long? = null,               // если пользователь есть в системе
    val name: String? = null,               // если пользователь не зарегистрирован
    val role: OrganizerRole,                // MAIN, JUDGE, SECRETARY, etc.
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
