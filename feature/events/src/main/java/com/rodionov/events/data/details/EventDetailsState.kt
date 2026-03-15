package com.rodionov.events.data.details

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.ui.BaseState

data class EventDetailsState(
    val eventDetails: CyclicEventDetails? = null
) : BaseState