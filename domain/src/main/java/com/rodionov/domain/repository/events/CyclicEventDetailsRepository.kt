package com.rodionov.domain.repository.events

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails

interface CyclicEventDetailsRepository {

    suspend fun getEventDetails(eventId: String): Result<CyclicEventDetails?>


}