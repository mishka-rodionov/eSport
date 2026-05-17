package com.competra.remote.response.upload

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("url") val url: String
)
