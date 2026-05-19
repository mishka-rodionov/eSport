package com.competra.events.presentation.main

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.EventsNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.events.EventsFilter
import com.competra.domain.repository.LoadingRepository
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.events.EventsRepository
import com.competra.events.data.main.EventsAction
import com.competra.events.data.main.EventsState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class EventsViewModel(
    private val navigation: Navigation,
    private val eventsRepository: EventsRepository,
    private val networkErrorRepository: NetworkErrorRepository,
    private val loadingRepository: LoadingRepository
) : BaseViewModel<EventsState>(EventsState()) {

    override fun onAction(action: BaseAction) {

    }

    fun onAction(action: EventsAction) {
        when (action) {
            is EventsAction.EventClick -> {
                val id = action.eventId ?: return
                viewModelScope.launch {
                    navigation.navigate(
                        EventsNavigation.EventDetailsRoute(eventId = id)
                    )
                }
            }
            is EventsAction.OpenFilterDialog -> {
                updateState { copy(isFilterDialogOpen = true) }
            }
            is EventsAction.CloseFilterDialog -> {
                updateState { copy(isFilterDialogOpen = false) }
            }
            is EventsAction.ApplyFilter -> {
                updateState { copy(appliedFilter = action.filter, isFilterDialogOpen = false) }
                getEvents(action.filter)
            }
            is EventsAction.ResetFilter -> {
                val cleared = EventsFilter()
                updateState { copy(appliedFilter = cleared) }
                getEvents(cleared)
            }
        }
    }

    fun getEvents(filter: EventsFilter = stateValue.appliedFilter) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            eventsRepository.getEvents(filter = filter).onSuccess { events ->
                events?.also { list ->
                    updateState { copy(events = list.sortedByDescending { it.startDate }) }
                }
                loadingRepository.emit(false)
            }.onFailure {
                updateState { copy(isGlobalError = true) }
                handleFailure(it)
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

@Parcelize
data class DetailsInfo(
    val title: String,
    val description: String
) : Parcelable
