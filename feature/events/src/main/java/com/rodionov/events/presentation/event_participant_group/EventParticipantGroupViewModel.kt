package com.rodionov.events.presentation.event_participant_group

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.PendingRegistrationRepository
import com.rodionov.data.navigation.TabRoutes
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.events.data.event_participant_group.EventParticipantGroupState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * Вьюмодель экрана группы участников события.
 * @property repository Репозиторий для получения данных о событии.
 * @property userRepository Репозиторий пользователя.
 * @property navigation Сервис навигации.
 * @property pendingRegistrationRepository Хранилище отложенного действия регистрации.
 */
class EventParticipantGroupViewModel(
    private val repository: CyclicEventDetailsRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val pendingRegistrationRepository: PendingRegistrationRepository
) : BaseViewModel<EventParticipantGroupState>(EventParticipantGroupState()) {

    private var currentUser: User? = null

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventParticipantGroupAction.RegisterUser -> registerUser()
            is EventParticipantGroupAction.CancelRegistration -> cancelRegistration()
        }
    }

    /**
     * Инициализация данных экрана.
     * @param eventId Идентификатор события.
     * @param group Данные группы участников.
     */
    fun initialize(eventId: Long, group: EventParticipantGroup) {
        updateState { copy(eventId = eventId, participantGroup = group, isLoading = true) }
        viewModelScope.launch {
            currentUser = userRepository.retrieveUser().getOrNull()

            repository.getParticipants(eventId, group.groupId)
                .onSuccess { participants ->
                    val isRegistered = currentUser?.id?.let { userId ->
                        participants.any { it.userId == userId }
                    } ?: false
                    updateState {
                        copy(
                            participants = participants,
                            isUserRegistered = isRegistered,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                }

            // Проверить отложенную регистрацию: если вернулись после авторизации
            val pending = pendingRegistrationRepository.pending.value
            if (pending != null && pending.eventId == eventId && pending.groupId == group.groupId) {
                pendingRegistrationRepository.clear()
                registerUser()
            }
        }
    }

    /**
     * Регистрация пользователя в группу.
     * Если пользователь не авторизован — сохраняет отложенное действие и переходит на Profile таб.
     */
    private fun registerUser() {
        val eventId = stateValue.eventId ?: return
        val group = stateValue.participantGroup ?: return

        viewModelScope.launch {
            if (!userRepository.isAuthorized()) {
                pendingRegistrationRepository.set(eventId, group.groupId)
                navigation.switchTab(TabRoutes.PROFILE)
                return@launch
            }

            val user = currentUser ?: return@launch
            updateState { copy(isRegistering = true) }
            repository.registerToEvent(
                eventId = eventId,
                groupId = group.groupId,
                firstName = user.firstName,
                lastName = user.lastName
            )
                .onSuccess {
                    updateState { copy(isRegistering = false, isUserRegistered = true) }
                }
                .onFailure {
                    updateState { copy(isRegistering = false) }
                }
        }
    }

    /**
     * Отмена регистрации пользователя.
     */
    private fun cancelRegistration() {
        val eventId = stateValue.eventId ?: return
        viewModelScope.launch {
            updateState { copy(isRegistering = true) }
            repository.cancelRegistration(eventId)
                .onSuccess {
                    updateState { copy(isRegistering = false, isUserRegistered = false) }
                }
                .onFailure {
                    updateState { copy(isRegistering = false) }
                }
        }
    }
}

/**
 * Действия на экране группы участников события.
 */
sealed interface EventParticipantGroupAction : BaseAction {
    data object RegisterUser : EventParticipantGroupAction
    data object CancelRegistration : EventParticipantGroupAction
}
