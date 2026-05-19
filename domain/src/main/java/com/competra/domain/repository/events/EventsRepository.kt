package com.competra.domain.repository.events

import com.competra.domain.models.Competition
import com.competra.domain.models.events.EventsFilter

interface EventsRepository {

    suspend fun getEvents(filter: EventsFilter = EventsFilter()): Result<List<Competition>?>

}
