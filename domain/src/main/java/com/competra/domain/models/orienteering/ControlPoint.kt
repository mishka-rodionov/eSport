package com.competra.domain.models.orienteering

/**
 * Модель контрольного пункта дистанции.
 *
 * Используется внутри доменного слоя и при расчёте результатов.
 *
 * @property number номер или код контрольного пункта.
 * @property role роль контрольного пункта (обычный, старт, финиш и т.д.).
 * @property score количество очков за взятие пункта.
 * Актуально для форматов с подсчётом баллов.
 * @property latitude широта КП (WGS84), приходит из геопривязанной карты при импорте IOF XML.
 * @property longitude долгота КП (WGS84), приходит из геопривязанной карты при импорте IOF XML.
 * Координаты нужны, чтобы считать длину перегона (и, соответственно, темп участника) между
 * соседними отметками — для дистанций без геопривязки или введённых вручную остаются null.
 */
data class ControlPoint(
    val number: Int,
    val role: ControlPointRole = ControlPointRole.ORDINARY,
    val score: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null
)
