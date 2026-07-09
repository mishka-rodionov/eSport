package com.competra.remote.request.clubs

import com.google.gson.annotations.SerializedName

data class CreateTeamRequest(
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String
)
