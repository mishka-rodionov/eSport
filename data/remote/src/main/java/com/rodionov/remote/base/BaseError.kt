package com.rodionov.remote.base

import com.google.gson.annotations.SerializedName

data class BaseError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: Int
)
