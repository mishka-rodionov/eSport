package com.rodionov.center.presentation.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.main.CenterState
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CenterViewModel(
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val orienteeringCompetitionRemoteRepository: OrienteeringCompetitionRemoteRepository,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor
) : BaseViewModel<CenterState>(CenterState()) {

    override fun onAction(action: BaseAction) {

    }

    fun handleEffects(effect: CenterEffects) {
        when (effect) {
            is CenterEffects.OpenKindOfSports -> {
                viewModelScope.launch {
                    navigation.navigate(CenterNavigation.KindOfSportRoute)
                }
            }

            is CenterEffects.OpenOrienteeringCreator -> viewModelScope.launch {
                navigation.navigate(CenterNavigation.CommonCompetitionFieldRoute())
//                navigation.navigate(CenterNavigation.OrienteeringCreatorRoute())
            }

            is CenterEffects.OpenOrienteeringEditor -> viewModelScope.launch {
                navigation.navigate(CenterNavigation.CommonCompetitionFieldRoute(competitionId = effect.competitionId))
//                navigation.navigate(CenterNavigation.OrienteeringCreatorRoute(competitionId = effect.competitionId))
            }

            is CenterEffects.OpenOrienteeringEventControl -> viewModelScope.launch {
                navigation.navigate(
                    CenterNavigation.OrienteeringEventControlRoute,
                    argument = navigation.createArguments(
                        EventsConstants.EVENT_ID.name to effect.competitionId
                    )
                )
            }

            is CenterEffects.ShowDeleteCompetitionDialog -> {
                updateState { copy(deletingCompetition = effect.competition) }
            }

            CenterEffects.HideDeleteCompetitionDialog -> {
                updateState { copy(deletingCompetition = null) }
            }

            is CenterEffects.DeleteCompetition -> {
                updateState { copy(deletingCompetition = null) }
                viewModelScope.launch(Dispatchers.IO) {
                    orienteeringCompetitionInteractor.deleteCompetition(effect.competition.localCompetitionId)
                        .onSuccess {
                            updateState {
                                copy(controlledEvents = controlledEvents.filter {
                                    it.localCompetitionId != effect.competition.localCompetitionId
                                })
                            }
                        }
                }
            }
        }
    }

    fun initialize() {
        viewModelScope.launch {
            val isAuthed = userRepository.isAuthorized()
            updateState {
                copy(isAuthed = isAuthed)
            }
            if (isAuthed) {
                userRepository.retrieveUser().onSuccess { user ->
                    orienteeringCompetitionInteractor.getCompetitionsByUserId(user.id)
                        .onSuccess { competitions ->
                            Log.d("LOG_TAG", "initialize: ${competitions.size}")
                            updateState {
                                copy(controlledEvents = competitions)
                            }
                        }
                        .onFailure {
                            Log.d("LOG_TAG", "initialize: $it")
                        }
//                    orienteeringCompetitionRemoteRepository.getCompetitionsByUserid(user.id)
//                        .onSuccess { competitions ->
//
//                            updateState {
//                                copy(controlledEvents = competitions)
//                            }
//                        }
                }
            }

        }
    }

}