package com.rodionov.remote.datasource.orienteering

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrienteeringCompetitionRemoteDataSource {

    @POST("url")
    suspend fun createOrienteeringCompetition(@Body request: OrienteeringCompetitionRequest): Result<CommonModel<OrienteeringCompetitionResponse>>

    @POST("url")
    suspend fun createCompetitionParticipantGroup(@Body request: List<ParticipantGroupRequest>): Result<CommonModel<List<ParticipantGroupResponse>>>

    @GET("event/orienteering/competitions")
    suspend fun getCompetitionsByUserid(@Query("userId") userId: String): Result<CommonModel<List<OrienteeringCompetitionResponse>>>

}