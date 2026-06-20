package com.competra.events.data.main

import com.competra.domain.models.events.EventsFilter

sealed class EventsAction {
    data class EventClick(val eventId: String?) : EventsAction()
    data object OpenFilterDialog : EventsAction()
    data object CloseFilterDialog : EventsAction()
    data class ApplyFilter(val filter: EventsFilter) : EventsAction()
    data object ResetFilter : EventsAction()
}
