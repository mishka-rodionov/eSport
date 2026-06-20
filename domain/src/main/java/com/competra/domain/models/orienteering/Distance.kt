package com.competra.domain.models.orienteering

/**
 * Модель дистанции соревнований по ориентированию.
 * 
 * @property id Локальный идентификатор дистанции.
 * @property remoteId Идентификатор дистанции на сервере.
 * @property competitionId Идентификатор соревнования, к которому относится дистанция.
 * @property name Название дистанции (например, "Длинная").
 * @property lengthMeters Протяженность в метрах.
 * @property climbMeters Набор высоты в метрах.
 * @property controlsCount Количество контрольных пунктов.
 * @property description Описание параметров дистанции.
 * @property isSynced Флаг синхронизации с сервером.
 * @property lastModified Время последнего изменения.
 * @property isDeleted Флаг пометки на удаление.
 * @property controlPoints Список контрольных пунктов на дистанции.
 * @property finishControlPoint Номер финишного контрольного пункта. Отметка этого КП в чипе используется как
 *                              время финиша участника. Для электронных систем отметки (SPORTIDUINO, SPORTIDENT,
 *                              SFR) поле обязательно к заполнению в UI; для бумажных/механических систем
 *                              может быть `null`.
 */
data class Distance(
    val id: Long = 0,
    val remoteId: Long? = null,
    val competitionId: String,
    val name: String? = null,
    val lengthMeters: Int,
    val climbMeters: Int,
    val controlsCount: Int,
    val description: String? = null,
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val serverUpdatedAt: Long? = null,
    val syncError: String? = null,
    val controlPoints: List<ControlPoint>,
    val finishControlPoint: Int? = null
)
