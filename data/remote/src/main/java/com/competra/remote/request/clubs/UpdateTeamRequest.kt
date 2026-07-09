package com.competra.remote.request.clubs

import com.google.gson.annotations.SerializedName

data class UpdateTeamRequest(
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String
)
