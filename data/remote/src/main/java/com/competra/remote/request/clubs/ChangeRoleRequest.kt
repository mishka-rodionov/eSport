package com.competra.remote.request.clubs

import com.google.gson.annotations.SerializedName

data class ChangeRoleRequest(
    @SerializedName("role") val role: String
)
