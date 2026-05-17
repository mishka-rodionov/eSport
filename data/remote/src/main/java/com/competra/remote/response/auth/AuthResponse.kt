package com.competra.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.competra.remote.response.user.UserResponse

data class AuthResponse(
    @SerializedName("user")
    val user: UserResponse,
    @SerializedName("token")
    val token: TokenResponse
)