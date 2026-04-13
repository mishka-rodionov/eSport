package com.rodionov.events.presentation.main

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.events.data.main.EventsAction
import com.rodionov.events.data.main.EventsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class EventsViewModel(
    private val navigation: Navigation,
    private val eventsRepository: EventsRepository,
    private val networkErrorRepository: NetworkErrorRepository
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
        }
    }

    fun getEvents(kindOfSports: List<KindOfSport> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.getEvents(kindOfSport = kindOfSports).onSuccess { events ->
                events?.also { list ->
                    updateState { copy(events = list) }
                }
            }.onFailure {
                updateState { copy(isGlobalError = true) }
                handleFailure(it)
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
