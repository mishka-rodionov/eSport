package com.rodionov.remote.response.user

import com.google.gson.annotations.SerializedName
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.SportsCategory

data class QualificationResponse(
    @SerializedName("kind_of_sport")
    val kindOfSport: KindOfSport,
    @SerializedName("sports_category")
    val sportsCategory: SportsCategory
)