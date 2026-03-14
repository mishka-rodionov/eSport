package com.rodionov.domain.repository.events

interface OrienteeringEventDetailsRepository {

    suspend fun getEventDetails(eventId: String): Result<OrienteeringEventDetails?>


}