package com.competra.center.presentation.splits

import androidx.lifecycle.viewModelScope
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.splits.ParticipantSplitsState
import com.competra.data.navigation.CenterNavigation
import com.competra.data.navigation.Navigation
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParticipantSplitsViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
) : BaseViewModel<ParticipantSplitsState>(ParticipantSplitsState()) {

    override fun onAction(action: BaseAction) {}

    fun load(participantId: String, competitionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val participant = interactor.getParticipants(competitionId)
                .getOrNull()
                ?.firstOrNull { it.id == participantId }
            val result = interactor.getResultByParticipantId(participantId)
            updateState { copy(participant = participant, result = result) }
        }
    }
}
