package com.competra.remote.datasource.auth

import com.competra.remote.base.CommonModel
import com.competra.remote.request.auth.AuthCodeRequest
import com.competra.remote.request.auth.EmailRequest
import com.competra.remote.request.auth.RefreshRequest
import com.competra.remote.request.user.UserProfileUpdateRequest
import com.competra.remote.request.user.UserRequest
import com.competra.remote.response.auth.AuthResponse
import com.competra.remote.response.user.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthRemoteDataSource {

    @POST("user/login")
    suspend fun login(@Body emailRequest: EmailRequest): Result<CommonModel<Any>>

    @POST("user/verify_code")
    suspend fun sendAuthCode(@Body authCodeRequest: AuthCodeRequest): Result<CommonModel<AuthResponse>>

    @POST("refresh_token")
    suspend fun refreshToken(@Body refreshRequest: RefreshRequest): Result<CommonModel<AuthResponse>>

    @POST("user/register")
    suspend fun register(@Body userRequest: UserRequest): Result<CommonModel<Any>>

    @GET("user/profile")
    suspend fun getProfile(): Result<CommonModel<UserResponse>>

    @PATCH("user/profile")
    suspend fun updateProfile(@Body request: UserProfileUpdateRequest): Result<CommonModel<UserResponse>>

}