package com.competra.remote.datasource.clubs

import com.competra.remote.base.CommonModel
import com.competra.remote.base.PagedResponse
import com.competra.remote.request.clubs.ChangeRoleRequest
import com.competra.remote.request.clubs.CreateClubRequest
import com.competra.remote.request.clubs.ReviewJoinRequestRequest
import com.competra.remote.request.clubs.UpdateClubRequest
import com.competra.remote.response.clubs.ClubJoinRequestResponse
import com.competra.remote.response.clubs.ClubMemberResponse
import com.competra.remote.response.clubs.ClubResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClubRemoteDataSource {

    @GET("clubs")
    suspend fun search(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Result<CommonModel<PagedResponse<ClubResponse>>>

    @GET("clubs/{id}")
    suspend fun getById(@Path("id") id: String): Result<CommonModel<ClubResponse>>

    @GET("clubs/mine")
    suspend fun listMine(): Result<CommonModel<List<ClubResponse>>>

    @POST("clubs")
    suspend fun create(@Body request: CreateClubRequest): Result<CommonModel<ClubResponse>>

    @PUT("clubs/{id}")
    suspend fun update(@Path("id") id: String, @Body request: UpdateClubRequest): Result<CommonModel<ClubResponse>>

    @DELETE("clubs/{id}")
    suspend fun delete(@Path("id") id: String): Result<CommonModel<Any>>

    @GET("clubs/{id}/members")
    suspend fun getMembers(@Path("id") id: String): Result<CommonModel<List<ClubMemberResponse>>>

    @DELETE("clubs/{id}/members/{userId}")
    suspend fun removeMember(@Path("id") id: String, @Path("userId") userId: String): Result<CommonModel<Any>>

    @PUT("clubs/{id}/members/{userId}/role")
    suspend fun changeMemberRole(
        @Path("id") id: String,
        @Path("userId") userId: String,
        @Body request: ChangeRoleRequest
    ): Result<CommonModel<ClubMemberResponse>>

    @POST("clubs/{id}/join-requests")
    suspend fun createJoinRequest(@Path("id") id: String): Result<CommonModel<ClubJoinRequestResponse>>

    @GET("clubs/{id}/join-requests")
    suspend fun listJoinRequestsForClub(@Path("id") id: String): Result<CommonModel<List<ClubJoinRequestResponse>>>

    @GET("clubs/join-requests/mine")
    suspend fun listMyJoinRequests(): Result<CommonModel<List<ClubJoinRequestResponse>>>

    @PUT("clubs/{id}/join-requests/{requestId}")
    suspend fun reviewJoinRequest(
        @Path("id") id: String,
        @Path("requestId") requestId: String,
        @Body request: ReviewJoinRequestRequest
    ): Result<CommonModel<ClubJoinRequestResponse>>
}
