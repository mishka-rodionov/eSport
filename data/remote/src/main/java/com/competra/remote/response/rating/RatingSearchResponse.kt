package com.competra.remote.response.rating

import com.google.gson.annotations.SerializedName

data class RatingSearchResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("ownerClubId") val ownerClubId: String,
    @SerializedName("ownerClubName") val ownerClubName: String,
    @SerializedName("createdAt") val createdAt: Long
)
