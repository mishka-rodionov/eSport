package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана деталей события.
 * Управляет загрузкой данных события, навигацией и процессом регистрации.
 *
 * @param cyclicEventDetailsRepository Репозиторий для получения деталей события.
 * @param userRepository Репозиторий пользователя.
 * @param navigation Сервис навигации.
 */
class EventDetailsViewModel(
    private val cyclicEventDetailsRepository: CyclicEventDetailsRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(eventDetails = null)
) {

    private var currentUser: User? = null

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventDetailsAction.OnGroupClick -> navigateToGroup(action.group)
            is EventDetailsAction.ToResults -> navigateToResults()
            is EventDetailsAction.ShowRegistrationDialog -> showRegistrationDialog()
            is EventDetailsAction.HideRegistrationDialog -> hideRegistrationDialog()
            is EventDetailsAction.SelectGroup -> selectGroup(action.group)
            is EventDetailsAction.ConfirmRegistration -> confirmRegistration()
            is EventDetailsAction.CancelRegistration -> cancelRegistration()
        }
    }

    /**
     * Инициализация экрана. Загрузка пользователя и детальной информации о событии.
     * @param eventId Идентификатор события.
     */
    fun initialize(eventId: String) {
        viewModelScope.launch {
            currentUser = userRepository.retrieveUser().getOrNull()

            cyclicEventDetailsRepository.getEventDetails(eventId, currentUser?.id)
                .onSuccess { details ->
                    updateState {
                        copy(
                            eventDetails = details,
                            isUserRegistered = details?.isUserRegistered ?: false
                        )
                    }
                }
                .onFailure {
                    // TODO: Обработка ошибки загрузки
                }
        }
    }

    private fun showRegistrationDialog() {
        updateState { copy(isRegistrationSheetVisible = true) }
    }

    private fun hideRegistrationDialog() {
        updateState { copy(isRegistrationSheetVisible = false, selectedGroup = null) }
    }

    private fun selectGroup(group: EventParticipantGroup) {
        updateState { copy(selectedGroup = group) }
    }

    /**
     * Подтверждение регистрации с данными текущего пользователя.
     */
    private fun confirmRegistration() {
        val selectedGroup = stateValue.selectedGroup ?: return
        val eventId = stateValue.eventDetails?.eventId ?: return
        val user = currentUser ?: return

        viewModelScope.launch {
            updateState { copy(isRegistering = true, error = null) }
            cyclicEventDetailsRepository.registerToEvent(
                eventId = eventId,
                groupId = selectedGroup.groupId,
                firstName = user.firstName,
                lastName = user.lastName
            )
                .onSuccess {
                    updateState {
                        copy(
                            isRegistering = false,
                            isRegistrationSheetVisible = false,
                            selectedGroup = null,
                            isUserRegistered = true
                        )
                    }
                }
                .onFailure { e ->
                    updateState {
                        copy(
                            isRegistering = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    /**
     * Отмена регистрации.
     */
    private fun cancelRegistration() {
        val eventId = stateValue.eventDetails?.eventId ?: return

        viewModelScope.launch {
            updateState { copy(isRegistering = true, error = null) }
            cyclicEventDetailsRepository.cancelRegistration(eventId)
                .onSuccess {
                    updateState { copy(isRegistering = false, isUserRegistered = false) }
                }
                .onFailure { e ->
                    updateState { copy(isRegistering = false, error = e.message) }
                }
        }
    }

    private fun navigateToGroup(group: EventParticipantGroup) {
        val eventId = stateValue.eventDetails?.eventId ?: return
        viewModelScope.launch {
            navigation.navigate(
                EventsNavigation.EventParticipantGroupRoute(
                    eventId = eventId,
                    participantGroup = group
                )
            )
        }
    }

    private fun navigateToResults() {
        val eventId = stateValue.eventDetails?.eventId ?: return
        viewModelScope.launch {
            navigation.navigate(EventsNavigation.EventResultsRoute(eventId = eventId))
        }
    }
}

/**
 * Действия на экране деталей события.
 */
sealed interface EventDetailsAction : BaseAction {
    data class OnGroupClick(val group: EventParticipantGroup) : EventDetailsAction
    data object ToResults : EventDetailsAction
    data object ShowRegistrationDialog : EventDetailsAction
    data object HideRegistrationDialog : EventDetailsAction
    data class SelectGroup(val group: EventParticipantGroup) : EventDetailsAction
    data object ConfirmRegistration : EventDetailsAction
    data object CancelRegistration : EventDetailsAction
}
