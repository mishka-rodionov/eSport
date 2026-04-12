package com.rodionov.remote.datasource.events

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.events.RegisterEventRequest
import com.rodionov.remote.response.events.CompetitionDetailResponse
import com.rodionov.remote.response.events.ParticipantPublicResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CyclicEventDetailsRemoteDataSource {

    @GET("event/orienteering/competitions/public/{eventId}")
    suspend fun getEventDetails(
        @Path("eventId") eventId: Long,
        @Query("userId") userId: String? = null
    ): Result<CommonModel<CompetitionDetailResponse>>

    @POST("event/orienteering/register")
    suspend fun registerToEvent(@Body request: RegisterEventRequest): Result<CommonModel<Unit>>

    @DELETE("event/orienteering/register/{competitionId}")
    suspend fun cancelRegistration(@Path("competitionId") competitionId: Long): Result<CommonModel<Unit>>

    @GET("event/orienteering/participants")
    suspend fun getParticipantsByGroup(@Query("groupId") groupId: String): Result<CommonModel<List<ParticipantPublicResponse>>>

}
