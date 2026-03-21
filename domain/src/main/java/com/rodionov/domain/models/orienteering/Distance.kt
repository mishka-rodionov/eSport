package com.rodionov.domain.models.orienteering

data class Distance(
    val remoteId: Long? = null,
    val competitionId: Long,
    val name: String? = null,               // например, "Длинная дистанция"
    val lengthMeters: Int,                  // длина в метрах
    val climbMeters: Int,                   // набор высоты
    val controlsCount: Int,                 // количество КП
    val description: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val controlPoints: List<ControlPoint>
)
