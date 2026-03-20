package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана деталей события.
 * Управляет загрузкой данных события, навигацией и процессом регистрации.
 *
 * @param cyclicEventDetailsRepository Репозиторий для получения деталей события.
 * @param navigation Сервис навигации.
 */
class EventDetailsViewModel(
    private val cyclicEventDetailsRepository: CyclicEventDetailsRepository,
    private val navigation: Navigation
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(eventDetails = null)
) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventDetailsAction.OnGroupClick -> navigateToGroup(action.group)
            is EventDetailsAction.ToResults -> navigateToResults()
            is EventDetailsAction.ShowRegistrationDialog -> showRegistrationDialog()
            is EventDetailsAction.HideRegistrationDialog -> hideRegistrationDialog()
            is EventDetailsAction.SelectGroup -> selectGroup(action.group)
            is EventDetailsAction.ConfirmRegistration -> confirmRegistration()
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

    /**
     * Показывает диалог регистрации.
     */
    private fun showRegistrationDialog() {
        updateState { copy(isRegistrationSheetVisible = true) }
    }

    /**
     * Скрывает диалог регистрации.
     */
    private fun hideRegistrationDialog() {
        updateState { copy(isRegistrationSheetVisible = false, selectedGroup = null) }
    }

    /**
     * Выбор группы в диалоге.
     * @param group Выбранная группа.
     */
    private fun selectGroup(group: EventParticipantGroup) {
        updateState { copy(selectedGroup = group) }
    }

    /**
     * Подтверждение регистрации.
     * Имитирует сетевой запрос и локальное сохранение.
     */
    private fun confirmRegistration() {
        val selectedGroup = stateValue.selectedGroup ?: return
        val eventId = stateValue.eventDetails?.eventId ?: return

        viewModelScope.launch {
            updateState { copy(isRegistering = true) }
            
            // Имитация сетевого запроса к серверу (Mock)
            // cyclicEventDetailsRepository.registerToEvent(eventId, selectedGroup.id)
            delay(2000) 

            // Имитация локального добавления пользователя в таблицу участников
            // localRepository.addParticipant(user, eventId, selectedGroup)
            
            updateState { 
                copy(
                    isRegistering = false, 
                    isRegistrationSheetVisible = false,
                    selectedGroup = null
                ) 
            }
            
            // TODO: Показать уведомление об успешной регистрации
        }
    }

    /**
     * Переход к деталям группы участников.
     * @param group Модель группы.
     */
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

    /**
     * Переход на экран результатов события.
     */
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
    /** Клик по группе участников. */
    data class OnGroupClick(val group: EventParticipantGroup) : EventDetailsAction
    /** Переход к результатам события. */
    data object ToResults : EventDetailsAction
    /** Показать диалог регистрации. */
    data object ShowRegistrationDialog : EventDetailsAction
    /** Скрыть диалог регистрации. */
    data object HideRegistrationDialog : EventDetailsAction
    /** Выбрать группу. */
    data class SelectGroup(val group: EventParticipantGroup) : EventDetailsAction
    /** Подтвердить регистрацию. */
    data object ConfirmRegistration : EventDetailsAction
}
