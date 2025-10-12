package com.rodionov.center.data.participant_list

import com.rodionov.local.entities.orienteering.ParticipantGroupWithParticipants
import com.rodionov.ui.BaseState

data class ParticipantListState(
    val participantGroupWithParticipants: List<ParticipantGroupWithParticipants> = emptyList()
): BaseState
