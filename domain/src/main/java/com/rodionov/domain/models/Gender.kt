package com.rodionov.domain.models

import com.google.gson.annotations.SerializedName

enum class Gender {
    @SerializedName("male")
    MALE,

    @SerializedName("female")
    FEMALE
}