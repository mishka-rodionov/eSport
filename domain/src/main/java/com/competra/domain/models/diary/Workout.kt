package com.competra.domain.models.diary

/**
 * Запись тренировочного дневника — общие для всех видов спорта поля.
 *
 * @property id Локальный идентификатор.
 * @property remoteId Идентификатор на сервере.
 * @property sportType Вид спорта.
 * @property status Запланирована или выполнена.
 * @property scheduledDate Дата, на которую запланирована тренировка (для [WorkoutStatus.PLANNED]).
 * @property startedAt Время начала выполненной тренировки.
 * @property durationSeconds Длительность тренировки в секундах.
 * @property distanceMeters Дистанция в метрах.
 * @property elevationGainMeters Набор высоты в метрах.
 * @property notes Произвольная заметка пользователя.
 * @property isSynced Флаг синхронизации с сервером.
 * @property lastModified Время последнего локального изменения.
 * @property isDeleted Флаг пометки на удаление (soft-delete).
 * @property serverUpdatedAt Время последнего подтверждённого сервером изменения.
 * @property syncError Текст последней ошибки синхронизации.
 */
data class Workout(
    val id: Long = 0,
    val remoteId: Long? = null,
    val sportType: SportType,
    val status: WorkoutStatus,
    val scheduledDate: Long? = null,
    val startedAt: Long? = null,
    val durationSeconds: Int? = null,
    val distanceMeters: Int? = null,
    val elevationGainMeters: Int? = null,
    val notes: String? = null,
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val serverUpdatedAt: Long? = null,
    val syncError: String? = null
)
