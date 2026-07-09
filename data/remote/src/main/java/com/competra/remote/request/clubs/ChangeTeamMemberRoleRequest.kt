package com.competra.remote.request.clubs

import com.google.gson.annotations.SerializedName

data class ChangeTeamMemberRoleRequest(
    @SerializedName("role") val role: String
)
