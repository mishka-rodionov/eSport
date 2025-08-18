package com.rodionov.events.data.main

sealed class EventsAction {
    data class EventClick(val eventId: Int) : EventsAction()
}