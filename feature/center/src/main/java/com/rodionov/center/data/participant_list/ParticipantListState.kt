package com.rodionov.center.data.participant_list

import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import com.rodionov.local.entities.orienteering.ParticipantGroupWithParticipants
import com.rodionov.ui.BaseState

data class ParticipantListState(
    val participantGroupWithParticipants: List<ParticipantGroupParticipants> = emptyList()
): BaseState
