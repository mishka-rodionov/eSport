package com.competra.domain.repository.events

import com.competra.domain.models.Competition
import com.competra.domain.models.KindOfSport

interface EventsRepository {

    suspend fun getEvents(kindOfSport: List<KindOfSport>): Result<List<Competition>?>

}