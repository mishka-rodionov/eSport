package com.rodionov.domain.models.orienteering

data class Stage(
    val remoteId: Long? = null,
    val competitionId: Long,
    val stageNumber: Int,
    val startDate: Long,
    val endDate: Long? = null,
    val description: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
