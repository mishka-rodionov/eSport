package com.rodionov.events.presentation.eventDetails

import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.Competition
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants

class EventDetailsViewModel(
    private val cyclicEventDetailsRepository: CyclicEventDetailsRepository,
    private val navigation: Navigation
) : BaseViewModel<EventDetailsState>(
    EventDetailsState(
        eventDetails = navigation.getArguments<Competition>(EventsConstants.EVENT_ID.name)
    )
) {

    override fun onAction(action: BaseAction) {

    }
}