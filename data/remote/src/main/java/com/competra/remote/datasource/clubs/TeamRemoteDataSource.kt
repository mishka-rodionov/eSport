package com.competra.remote.datasource.clubs

import com.competra.remote.base.CommonModel
import com.competra.remote.request.clubs.AddTeamMemberRequest
import com.competra.remote.request.clubs.ChangeTeamMemberRoleRequest
import com.competra.remote.request.clubs.CreateTeamRequest
import com.competra.remote.request.clubs.UpdateTeamRequest
import com.competra.remote.response.clubs.TeamMemberResponse
import com.competra.remote.response.clubs.TeamResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TeamRemoteDataSource {

    @GET("clubs/{clubId}/teams")
    suspend fun listByClub(@Path("clubId") clubId: String): Result<CommonModel<List<TeamResponse>>>

    @GET("teams/{id}")
    suspend fun getById(@Path("id") id: String): Result<CommonModel<TeamResponse>>

    @POST("clubs/{clubId}/teams")
    suspend fun create(
        @Path("clubId") clubId: String,
        @Body request: CreateTeamRequest
    ): Result<CommonModel<TeamResponse>>

    @PUT("teams/{id}")
    suspend fun update(@Path("id") id: String, @Body request: UpdateTeamRequest): Result<CommonModel<TeamResponse>>

    @DELETE("teams/{id}")
    suspend fun delete(@Path("id") id: String): Result<CommonModel<Any>>

    @GET("teams/{id}/members")
    suspend fun getMembers(@Path("id") id: String): Result<CommonModel<List<TeamMemberResponse>>>

    @POST("teams/{id}/members")
    suspend fun addMember(
        @Path("id") id: String,
        @Body request: AddTeamMemberRequest
    ): Result<CommonModel<TeamMemberResponse>>

    @DELETE("teams/{id}/members/{clubMemberId}")
    suspend fun removeMember(
        @Path("id") id: String,
        @Path("clubMemberId") clubMemberId: String
    ): Result<CommonModel<Any>>

    @PUT("teams/{id}/members/{clubMemberId}/role")
    suspend fun changeMemberRole(
        @Path("id") id: String,
        @Path("clubMemberId") clubMemberId: String,
        @Body request: ChangeTeamMemberRoleRequest
    ): Result<CommonModel<TeamMemberResponse>>
}
