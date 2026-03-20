package com.rodionov.events.presentation.event_participant_group

import androidx.lifecycle.viewModelScope
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.events.data.event_participant_group.EventParticipantGroupState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Вьюмодель экрана группы участников события.
 * @property repository Репозиторий для получения данных о событии.
 */
class EventParticipantGroupViewModel(
    private val repository: CyclicEventDetailsRepository
) : BaseViewModel<EventParticipantGroupState>(EventParticipantGroupState()) {

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
        updateState { copy(participantGroup = group, isLoading = true) }
        viewModelScope.launch {
            repository.getParticipants(eventId, group.groupId)
                .onSuccess { participants ->
                    updateState { copy(participants = participants, isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                    // TODO: Добавить обработку ошибок
                }
        }
    }

    /**
     * Регистрация пользователя в группу.
     */
    private fun registerUser() {
        val group = stateValue.participantGroup ?: return
        viewModelScope.launch {
            updateState { copy(isRegistering = true) }
            
            // Имитация сетевого запроса к серверу (Mock)
            // repository.registerToGroup(group.eventId, group.groupId)
            delay(2000) 

            updateState { 
                copy(
                    isRegistering = false, 
                    isUserRegistered = true 
                ) 
            }
            
            // TODO: После успешного запроса можно обновить список участников
        }
    }

    /**
     * Отмена регистрации пользователя в группе.
     */
    private fun cancelRegistration() {
        val group = stateValue.participantGroup ?: return
        viewModelScope.launch {
            updateState { copy(isRegistering = true) }

            // Имитация сетевого запроса на отмену регистрации (Mock)
            // repository.cancelRegistration(group.eventId, group.groupId)
            delay(2000)

            updateState {
                copy(
                    isRegistering = false,
                    isUserRegistered = false
                )
            }
        }
    }
}

/**
 * Действия на экране группы участников события.
 */
sealed interface EventParticipantGroupAction : BaseAction {
    /** Зарегистрировать пользователя в группу. */
    data object RegisterUser : EventParticipantGroupAction
    /** Отменить регистрацию пользователя. */
    data object CancelRegistration : EventParticipantGroupAction
}
