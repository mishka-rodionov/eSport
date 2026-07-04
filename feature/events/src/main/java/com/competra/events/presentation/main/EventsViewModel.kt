package com.competra.events.presentation.main

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
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
    private val loadingRepository: LoadingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventsState>(EventsState()) {

    override fun onAction(action: BaseAction) {

    }

    fun onAction(action: EventsAction) {
        when (action) {
            is EventsAction.EventClick -> {
                val id = action.eventId ?: return
                analytics.trackEvent(
                    AnalyticsEvent.EventOpened(
                        eventId = id,
                        source = AnalyticsEvent.EventSource.LIST,
                    )
                )
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
                analytics.trackEvent(AnalyticsEvent.EventFilterApplied(action.filter.toAnalyticsParams()))
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
            updateState { copy(isLoading = true, isGlobalError = false) }
            loadingRepository.emit(true)
            eventsRepository.getEvents(filter = filter).onSuccess { events ->
                events?.also { list ->
                    updateState { copy(events = list.sortedByDescending { it.startDate }) }
                }
                updateState { copy(isLoading = false) }
                loadingRepository.emit(false)
            }.onFailure {
                updateState { copy(isGlobalError = true, isLoading = false) }
                handleFailure(it)
                loadingRepository.emit(false)
            }
        }
    }

    private fun EventsFilter.toAnalyticsParams(): Map<String, Any?> = mapOf(
        "kinds_count" to kindOfSports.size,
        "statuses_count" to statuses.size,
        "has_date_from" to (dateFrom != null),
        "has_date_to" to (dateTo != null),
        "include_test" to includeTest,
    )

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
