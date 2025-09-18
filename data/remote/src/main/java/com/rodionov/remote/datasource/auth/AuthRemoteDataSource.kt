package com.rodionov.remote.datasource.auth

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.auth.AuthCodeRequest
import com.rodionov.remote.request.auth.EmailRequest
import com.rodionov.remote.request.auth.RefreshRequest
import com.rodionov.remote.request.user.UserRequest
import com.rodionov.remote.response.auth.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRemoteDataSource {

    @POST("user/login")
    suspend fun login(@Body emailRequest: EmailRequest): Result<CommonModel<Any>>

    @POST("user/verify_code")
    suspend fun sendAuthCode(@Body authCodeRequest: AuthCodeRequest): Result<CommonModel<AuthResponse>>

    @POST("refresh_token")
    suspend fun refreshToken(@Body refreshRequest: RefreshRequest): Result<CommonModel<AuthResponse>>

    @POST("user/register")
    suspend fun register(@Body userRequest: UserRequest): Result<CommonModel<AuthResponse>>

}