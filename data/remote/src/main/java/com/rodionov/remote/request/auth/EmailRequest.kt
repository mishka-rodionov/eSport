package com.rodionov.remote.request.auth

import com.google.gson.annotations.SerializedName

data class EmailRequest(
    @SerializedName("email")
    private val email: String
)
