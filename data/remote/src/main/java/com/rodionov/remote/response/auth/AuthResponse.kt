package com.rodionov.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.rodionov.remote.response.user.UserResponse

data class AuthResponse(
    @SerializedName("user")
    val user: UserResponse,
    @SerializedName("token")
    val token: TokenResponse
)