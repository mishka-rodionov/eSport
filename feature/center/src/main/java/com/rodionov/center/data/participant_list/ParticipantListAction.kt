package com.rodionov.center.data.participant_list

import com.rodionov.ui.BaseAction

sealed class ParticipantListAction : BaseAction {

    data class ShowCreateParticipantDialog(val group: Int): ParticipantListAction()
    data object HideCreateParticipantDialog: ParticipantListAction()

    data class CreateNewParticipant(val group: Int, val firstName: String, val secondName: String) :
        ParticipantListAction()

}
