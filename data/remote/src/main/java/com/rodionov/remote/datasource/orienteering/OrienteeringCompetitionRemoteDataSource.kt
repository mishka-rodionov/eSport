package com.rodionov.remote.datasource.orienteering

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.OrienteeringParticipantRequest
import com.rodionov.remote.request.orienteering.OrienteeringResultRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupPublishRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.OrienteeringParticipantResponse
import com.rodionov.remote.response.orienteering.OrienteeringResultResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrienteeringCompetitionRemoteDataSource {

    @POST("event/orienteering/save/competitions")
    suspend fun createOrienteeringCompetition(@Body request: OrienteeringCompetitionRequest): Result<CommonModel<OrienteeringCompetitionResponse>>

    @POST("event/orienteering/save/participantGroup")
    suspend fun createCompetitionParticipantGroup(@Body request: List<ParticipantGroupRequest>): Result<CommonModel<List<ParticipantGroupResponse>>>

    @POST("event/orienteering/save/participantGroup")
    suspend fun publishParticipantGroups(@Body request: List<ParticipantGroupPublishRequest>): Result<CommonModel<List<ParticipantGroupResponse>>>

    @GET("event/orienteering/competitions")
    suspend fun getCompetitionsByUserid(@Query("userId") userId: String): Result<CommonModel<List<OrienteeringCompetitionResponse>>>

    @POST("event/orienteering/save/participant")
    suspend fun saveParticipant(@Body request: OrienteeringParticipantRequest): Result<CommonModel<OrienteeringParticipantResponse>>

    @POST("event/orienteering/save/result")
    suspend fun saveResult(@Body request: OrienteeringResultRequest): Result<CommonModel<OrienteeringResultResponse>>

}