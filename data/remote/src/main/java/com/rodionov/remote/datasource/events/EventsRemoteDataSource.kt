package com.rodionov.remote.datasource.events

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.response.competition.CompetitionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsRemoteDataSource {

    @GET("url")
    suspend fun getEvents(@Query("kind_of_sports") kindOfSport: List<String>): Result<CommonModel<List<CompetitionResponse>>>

}