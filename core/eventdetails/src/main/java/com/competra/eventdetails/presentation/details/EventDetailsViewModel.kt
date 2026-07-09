package com.competra.eventdetails.presentation.details

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.EventsNavigation
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.PendingRegistrationRepository
import com.competra.data.navigation.TabRoutes
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.cyclic_event.EventParticipantGroup
import com.competra.domain.models.user.User
import com.competra.domain.repository.LoadingRepository
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.events.CyclicEventDetailsRepository
import com.competra.domain.repository.user.UserRepository
import com.competra.eventdetails.data.details.EventDetailsState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана деталей события.
 * Управляет загрузкой данных события, навигацией и процессом регистрации.
 *
 * @param cyclicEventDetailsRepository Репозиторий для получения деталей события.
 * @param userRepository Репозиторий пользователя.
 * @param navigation Сервис навигации.
 * @param pendingRegistrationRepository Хранилище отложенного действия регистрации.
 * @param networkErrorRepository Репозиторий для передачи сетевых ошибок в MainActivity.
 */
class EventDetailsViewModel(
    private val cyclicEventDetailsRepository: CyclicEventDetailsRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val pendingRegistrationRepository: PendingRegistrationRepository,
    private val networkErrorRepository: NetworkErrorRepository,
    private val loadingRepository: LoadingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(eventDetails = null)
) {

    private var currentUser: User? = null

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventDetailsAction.OnGroupClick -> navigateToGroup(action.group)
            is EventDetailsAction.ToResults -> navigateToResults()
            is EventDetailsAction.ToLiveResults -> navigateToLiveResults()
            is EventDetailsAction.ShowRegistrationDialog -> showRegistrationDialog()
            is EventDetailsAction.HideRegistrationDialog -> hideRegistrationDialog()
            is EventDetailsAction.SelectGroup -> selectGroup(action.group)
            is EventDetailsAction.CommandNameChanged -> updateState { copy(commandName = action.commandName) }
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
            loadingRepository.emit(true)
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
                    handleFailure(it)
                }
            loadingRepository.emit(false)

            // Проверить отложенную регистрацию: если вернулись после авторизации
            val pending = pendingRegistrationRepository.pending.value
            if (pending != null && pending.eventId == eventId && pending.groupId == null) {
                pendingRegistrationRepository.clear()
                updateState { copy(isRegistrationSheetVisible = true) }
            }
        }
    }

    private fun showRegistrationDialog() {
        viewModelScope.launch {
            val eventId = stateValue.eventDetails?.eventId
            if (eventId != null) {
                analytics.trackEvent(AnalyticsEvent.EventRegisterClicked(eventId))
            }
            if (!userRepository.isAuthorized()) {
                if (eventId == null) return@launch
                pendingRegistrationRepository.set(eventId)
                navigation.switchTab(TabRoutes.PROFILE)
                return@launch
            }
            updateState { copy(isRegistrationSheetVisible = true) }
        }
    }

    private fun hideRegistrationDialog() {
        updateState { copy(isRegistrationSheetVisible = false, selectedGroup = null, commandName = "") }
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
                lastName = user.lastName,
                commandName = stateValue.commandName.trim().ifBlank { null }
            )
                .onSuccess {
                    updateState {
                        copy(
                            isRegistering = false,
                            isRegistrationSheetVisible = false,
                            selectedGroup = null,
                            commandName = "",
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
                    handleFailure(e)
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
                    handleFailure(e)
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

    private fun navigateToLiveResults() {
        val eventId = stateValue.eventDetails?.eventId ?: return
        analytics.trackEvent(AnalyticsEvent.EventLiveResultsOpened(eventId))
        viewModelScope.launch {
            navigation.navigate(EventsNavigation.LiveResultsRoute(eventId = eventId))
        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }
}

/**
 * Действия на экране деталей события.
 */
sealed interface EventDetailsAction : BaseAction {
    data class OnGroupClick(val group: EventParticipantGroup) : EventDetailsAction
    data object ToResults : EventDetailsAction
    data object ToLiveResults : EventDetailsAction
    data object ShowRegistrationDialog : EventDetailsAction
    data object HideRegistrationDialog : EventDetailsAction
    data class SelectGroup(val group: EventParticipantGroup) : EventDetailsAction
    data class CommandNameChanged(val commandName: String) : EventDetailsAction
    data object ConfirmRegistration : EventDetailsAction
    data object CancelRegistration : EventDetailsAction
}
