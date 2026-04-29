package com.rodionov.center.presentation.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.main.CenterState
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.LoadingRepository
import com.rodionov.domain.repository.NetworkErrorRepository
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
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val networkErrorRepository: NetworkErrorRepository,
    private val loadingRepository: LoadingRepository
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
                    loadingRepository.emit(true)
                    orienteeringCompetitionInteractor.deleteCompetition(effect.competition.localCompetitionId)
                        .onSuccess {
                            updateState {
                                copy(controlledEvents = controlledEvents.filter {
                                    it.localCompetitionId != effect.competition.localCompetitionId
                                })
                            }
                        }
                        .onFailure { handleFailure(it) }
                    loadingRepository.emit(false)
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
                loadingRepository.emit(true)
                userRepository.retrieveUser().onSuccess { user ->
                    orienteeringCompetitionInteractor.getCompetitionsByUserId(user.id)
                        .onSuccess { competitions ->
                            Log.d("LOG_TAG", "initialize: ${competitions.size}")
                            updateState {
                                copy(controlledEvents = competitions.sortedByDescending { it.competition.startDate })
                            }
                        }
                        .onFailure { handleFailure(it) }
//                    orienteeringCompetitionRemoteRepository.getCompetitionsByUserid(user.id)
//                        .onSuccess { competitions ->
//
//                            updateState {
//                                copy(controlledEvents = competitions)
//                            }
//                        }
                }.onFailure { handleFailure(it) }
                loadingRepository.emit(false)
            }

        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }

}
