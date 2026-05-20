package com.competra.remote.request.device

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("platform")
    val platform: String = "android",
    @SerializedName("appVersion")
    val appVersion: String? = null,
)
