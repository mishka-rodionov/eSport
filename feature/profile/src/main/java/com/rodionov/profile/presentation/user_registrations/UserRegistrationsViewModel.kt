package com.rodionov.profile.presentation.user_registrations

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.LoadingRepository
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.profile.data.user_registrations.UserRegistrationsAction
import com.rodionov.profile.data.user_registrations.UserRegistrationsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel экрана «Предстоящие старты».
 * Загружает список соревнований, на которые пользователь зарегистрирован участником,
 * и обрабатывает переходы.
 */
class UserRegistrationsViewModel(
    private val navigation: Navigation,
    private val competitionRepository: OrienteeringCompetitionRemoteRepository,
    private val loadingRepository: LoadingRepository,
    private val networkErrorRepository: NetworkErrorRepository
) : BaseViewModel<UserRegistrationsState>(UserRegistrationsState()) {

    init {
        load()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is UserRegistrationsAction.OnItemClick -> openEventDetails(action.eventId)
            UserRegistrationsAction.OnBackClick -> back()
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            loadingRepository.emit(true)
            competitionRepository.getRegisteredCompetitions()
                .onSuccess { list ->
                    updateState { copy(registrations = list, isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                    handleFailure(it)
                }
            loadingRepository.emit(false)
        }
    }

    private fun openEventDetails(eventId: Long) {
        viewModelScope.launch {
            navigation.navigate(EventsNavigation.EventDetailsRoute(eventId = eventId))
        }
    }

    private fun back() {
        viewModelScope.launch { navigation.back() }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }
}
