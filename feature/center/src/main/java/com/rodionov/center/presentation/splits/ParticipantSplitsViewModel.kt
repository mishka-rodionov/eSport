package com.rodionov.center.presentation.splits

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.splits.ParticipantSplitsState
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParticipantSplitsViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
) : BaseViewModel<ParticipantSplitsState>(ParticipantSplitsState()) {

    override fun onAction(action: BaseAction) {}

    fun load(participantId: String, competitionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val participant = interactor.getParticipants(competitionId)
                .getOrNull()
                ?.firstOrNull { it.id == participantId }
            val result = interactor.getResultByParticipantId(participantId)
            updateState { copy(participant = participant, result = result) }
        }
    }
}
