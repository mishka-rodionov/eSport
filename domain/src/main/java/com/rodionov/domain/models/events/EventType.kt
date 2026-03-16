package com.rodionov.domain.models.events

sealed interface EventType {

    sealed class CyclicEvent : EventType {
        data object Orienteering : CyclicEvent()
        data object Snowboarding : CyclicEvent()
        data object CrossCountry : CyclicEvent()
        data object MountainBiking : CyclicEvent()
        data object TrailRunning : CyclicEvent()
        data object RoadCycling : CyclicEvent()
    }

}