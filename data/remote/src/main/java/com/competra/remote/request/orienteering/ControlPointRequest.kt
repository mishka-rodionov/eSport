package com.competra.remote.request.orienteering

import com.google.gson.annotations.SerializedName
import com.competra.domain.models.orienteering.ControlPointRole

data class ControlPointRequest(
    @SerializedName("number")
    val number: Int,
    @SerializedName("role")
    val role: ControlPointRole = ControlPointRole.ORDINARY,
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null
)