package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName
import com.rodionov.domain.models.orienteering.ControlPointRole

data class ControlPointResponse(
    @SerializedName("number")
    val number: Int,
    @SerializedName("role")
    val role: ControlPointRole = ControlPointRole.ORDINARY,
    @SerializedName("score")
    val score: Int = 0
)
