package com.rodionov.center.presentation.main

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.main.CenterState
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.launch

class CenterViewModel(
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val orienteeringCompetitionRemoteRepository: OrienteeringCompetitionRemoteRepository
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
                navigation.navigate(CenterNavigation.OrienteeringCreatorRoute)
            }

            is CenterEffects.OpenOrienteeringEventControl -> viewModelScope.launch {
                navigation.navigate(
                    CenterNavigation.OrienteeringEventControlRoute,
                    argument = navigation.createArguments(
                        EventsConstants.EVENT_ID.name to effect.competitionId
                    )
                )
            }
        }
    }

    fun initialize() {
        viewModelScope.launch {
            val isAuthed = userRepository.isAuthorized()
            updateState {
                copy(isAuthed = userRepository.isAuthorized())
            }
            if (isAuthed) {
                userRepository.retrieveUser().onSuccess { user ->
                    orienteeringCompetitionRemoteRepository.getCompetitionsByUserid(user.id)
                        .onSuccess { competitions ->
                            updateState {
                                copy(controlledEvents = competitions)
                            }
                        }
                }
            }

        }
    }

}