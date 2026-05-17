package com.competra.events.data.main

sealed class EventsAction {
    data class EventClick(val eventId: Long?) : EventsAction()
}