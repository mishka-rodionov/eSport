package com.competra.remote.request.rating

import com.google.gson.annotations.SerializedName

data class AddCompetitionToRatingRequest(
    @SerializedName("competitionId") val competitionId: String
)
