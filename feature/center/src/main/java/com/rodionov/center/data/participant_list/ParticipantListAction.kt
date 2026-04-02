package com.rodionov.center.data.participant_list

import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.ui.BaseAction

/**
 * Действия на экране списка участников.
 */
sealed class ParticipantListAction : BaseAction {

    /**
     * Показать диалог создания нового участника.
     * @param group Индекс группы в списке.
     */
    data class ShowCreateParticipantDialog(val group: Int): ParticipantListAction()
    
    /**
     * Показать диалог редактирования участника.
     * @param group Индекс группы в списке.
     * @param participant Участник для редактирования.
     */
    data class ShowEditParticipantDialog(val group: Int, val participant: OrienteeringParticipant): ParticipantListAction()
    
    /**
     * Скрыть диалог.
     */
    data object HideCreateParticipantDialog: ParticipantListAction()

    /**
     * Создать нового участника.
     */
    data class CreateNewParticipant(val group: Int, val firstName: String, val secondName: String) :
        ParticipantListAction()

    /**
     * Обновить данные существующего участника.
     */
    data class UpdateParticipant(val participant: OrienteeringParticipant) : ParticipantListAction()

    /**
     * Показать диалог подтверждения удаления участника.
     * @param participant Участник для удаления.
     */
    data class ShowDeleteParticipantDialog(val participant: OrienteeringParticipant) : ParticipantListAction()

    /**
     * Скрыть диалог подтверждения удаления.
     */
    data object HideDeleteParticipantDialog : ParticipantListAction()

    /**
     * Удалить участника и пересчитать список группы.
     * @param participant Участник для удаления.
     */
    data class DeleteParticipant(val participant: OrienteeringParticipant) : ParticipantListAction()

}
