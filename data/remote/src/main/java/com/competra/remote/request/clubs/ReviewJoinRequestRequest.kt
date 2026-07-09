package com.competra.remote.request.clubs

import com.google.gson.annotations.SerializedName

data class ReviewJoinRequestRequest(
    @SerializedName("approve") val approve: Boolean
)
