package com.rodionov.domain.models.orienteering

import com.google.gson.annotations.SerializedName

enum class ControlPointRole {
    @SerializedName("ordinary")
    ORDINARY,
    @SerializedName("required")
    REQUIRED,
    @SerializedName("start")
    START,
    @SerializedName("finish")
    FINISH,
    @SerializedName("observation")
    OBSERVATION

}