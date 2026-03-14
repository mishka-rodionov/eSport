package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.SavedStateHandle
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.Competition
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants

class EventDetailsViewModel(
    private val navigation: Navigation
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(
        competition = navigation.getArguments<Competition>(EventsConstants.EVENT_ID.name)
    )
) {

    override fun onAction(action: BaseAction) {

    }
}