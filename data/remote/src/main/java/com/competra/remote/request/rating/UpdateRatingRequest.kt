package com.competra.remote.request.rating

import com.google.gson.annotations.SerializedName

data class UpdateRatingRequest(
    @SerializedName("name") val name: String,
    @SerializedName("groups") val groups: List<RatingGroupRequest>
)
