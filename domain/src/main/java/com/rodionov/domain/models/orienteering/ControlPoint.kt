package com.rodionov.domain.models.orienteering

/**
 * Модель контрольного пункта дистанции.
 *
 * Используется внутри доменного слоя и при расчёте результатов.
 *
 * @property number номер или код контрольного пункта.
 * @property role роль контрольного пункта (обычный, старт, финиш и т.д.).
 * @property score количество очков за взятие пункта.
 * Актуально для форматов с подсчётом баллов.
 */
data class ControlPoint(
    val number: Int,
    val role: ControlPointRole = ControlPointRole.ORDINARY,
    val score: Int = 0
)
