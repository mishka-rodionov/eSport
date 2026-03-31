package com.rodionov.remote.datasource.events

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.events.RegisterEventRequest
import com.rodionov.remote.response.events.CyclicEventDetailsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CyclicEventDetailsRemoteDataSource {

    @GET("event/cyclic/{eventId}")
    suspend fun getEventDetails(@Path("eventId") eventId: String): Result<CommonModel<CyclicEventDetailsResponse>>

    @POST("event/cyclic/register")
    suspend fun registerToEvent(@Body request: RegisterEventRequest): Result<CommonModel<Unit>>

    @DELETE("event/cyclic/register/{eventId}")
    suspend fun cancelRegistration(@Path("eventId") eventId: Long): Result<CommonModel<Unit>>

}
