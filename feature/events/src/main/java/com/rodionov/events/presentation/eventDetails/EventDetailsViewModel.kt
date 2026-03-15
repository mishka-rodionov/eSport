package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class EventDetailsViewModel(
    private val cyclicEventDetailsRepository: CyclicEventDetailsRepository,
    private val navigation: Navigation
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(eventDetails = null)
) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventDetailsAction.OnGroupClick -> navigateToGroup(action.group)
        }
    }

    /**
     * Инициализация экрана. Загрузка детальной информации о событии.
     * @param eventId Идентификатор события.
     */
    fun initialize(eventId: Long) {
        viewModelScope.launch {
            cyclicEventDetailsRepository.getEventDetails(eventId.toString())
                .onSuccess { details ->
                    updateState { copy(eventDetails = details) }
                }
                .onFailure {
                    // TODO: Обработка ошибки загрузки
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
}

sealed interface EventDetailsAction : BaseAction {
    data class OnGroupClick(val group: EventParticipantGroup) : EventDetailsAction
}
