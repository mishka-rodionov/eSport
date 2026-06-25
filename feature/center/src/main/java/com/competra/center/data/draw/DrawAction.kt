package com.competra.center.data.draw

import com.competra.ui.BaseAction

sealed class DrawAction : BaseAction {
    data object StartDrawOperation : DrawAction()
    data object StartGroupDrawOperation : DrawAction()

    /**
     * Жеребьёвка по дистанциям.
     *
     * @param corridors максимум одновременных стартов в одну стартовую минуту (≥ 1).
     * @param gap минимальный зазор между стартами участников одной дистанции
     *   в числе стартовых интервалов (≥ 1).
     */
    data class StartDistanceDrawOperation(val corridors: Int, val gap: Int) : DrawAction()
}
