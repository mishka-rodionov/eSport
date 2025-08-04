package com.rodionov.remote.datasource

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OrienteeringCompetitionRemoteDataSource {

    @POST("url")
    suspend fun createOrienteeringCompetition(@Body request: OrienteeringCompetitionRequest): Result<CommonModel<OrienteeringCompetitionResponse>>

    @POST("url")
    suspend fun createCompetitionParticipantGroup(@Body request: List<ParticipantGroupRequest>): Result<CommonModel<List<ParticipantGroupResponse>>>

}