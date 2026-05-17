package com.competra.remote.datasource.events

import com.competra.remote.base.CommonModel
import com.competra.remote.response.competition.CompetitionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsRemoteDataSource {

    @GET("event/orienteering/competitions/public")
    suspend fun getEvents(@Query("kind_of_sports") kindOfSport: List<String>): Result<CommonModel<List<CompetitionResponse>>>

}