package com.rodionov.remote.datasource.auth

import com.rodionov.remote.base.CommonModel
import com.rodionov.remote.request.auth.AuthCodeRequest
import com.rodionov.remote.request.auth.EmailRequest
import com.rodionov.remote.response.auth.AuthResponse
import com.rodionov.remote.response.auth.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRemoteDataSource {

    @POST("request_code")
    suspend fun requestAuthCode(@Body emailRequest: EmailRequest): Result<CommonModel<Any>>

    @POST("verify_code")
    suspend fun sendAuthCode(@Body authCodeRequest: AuthCodeRequest): Result<CommonModel<AuthResponse>>

}