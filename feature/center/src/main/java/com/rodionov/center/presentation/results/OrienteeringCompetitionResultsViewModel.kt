package com.rodionov.center.presentation.results

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.results.OrienteeringCompetitionResultsState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrienteeringCompetitionResultsViewModel(
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
): BaseViewModel<OrienteeringCompetitionResultsState>(OrienteeringCompetitionResultsState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    init {
        competitionId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val results = orienteeringCompetitionInteractor.getResultsByGroups(it).getOrNull() ?: emptyList()
                updateState { copy(
                    groupsWithParticipantsAndResults =  results
                ) }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        
    }
}