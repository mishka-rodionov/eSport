package com.rodionov.center.data.get_chip

import com.rodionov.ui.BaseAction

/**
 * Действия на экране выдачи чипов.
 */
sealed class GetOrienteeringChipAction : BaseAction {
    /**
     * Изменение номера чипа участника.
     * @param participantId ID участника.
     * @param chipNumber Новый номер чипа.
     */
    data class UpdateChipNumber(val participantId: Long, val chipNumber: String) : GetOrienteeringChipAction()

    /**
     * Изменение состояния выдачи чипа (чекбокс).
     * @param participantId ID участника.
     * @param isGiven Флаг - выдан чип или нет.
     */
    data class ToggleChipGiven(val participantId: Long, val isGiven: Boolean) : GetOrienteeringChipAction()

    /**
     * Сохранение измененных данных в локальную БД.
     */
    data object SaveChanges : GetOrienteeringChipAction()
}
