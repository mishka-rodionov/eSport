package com.competra.remote.datasource.events

import com.competra.remote.base.CommonModel
import com.competra.remote.response.competition.CompetitionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsRemoteDataSource {

    @GET("event/orienteering/competitions/public")
    suspend fun getEvents(
        @Query("kind_of_sports") kindOfSports: List<String> = emptyList(),
        @Query("statuses") statuses: List<String> = emptyList(),
        @Query("date_from") dateFrom: Long? = null,
        @Query("date_to") dateTo: Long? = null,
        @Query("includeTest") includeTest: Boolean = false
    ): Result<CommonModel<List<CompetitionResponse>>>

}
