package com.rodionov.events.presentation.event_participant_group

import androidx.lifecycle.viewModelScope
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.events.data.event_participant_group.EventParticipantGroupState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * Вьюмодель экрана группы участников события.
 * @property repository Репозиторий для получения данных о событии.
 */
class EventParticipantGroupViewModel(
    private val repository: CyclicEventDetailsRepository
) : BaseViewModel<EventParticipantGroupState>(EventParticipantGroupState()) {

    override fun onAction(action: BaseAction) {
        // Обработка действий пользователя
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
}
