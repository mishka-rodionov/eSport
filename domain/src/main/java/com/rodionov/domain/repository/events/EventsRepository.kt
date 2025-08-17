package com.rodionov.domain.repository.events

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.KindOfSport

interface EventsRepository {

    suspend fun getEvents(kindOfSport: List<KindOfSport>): Result<List<Competition>?>

}