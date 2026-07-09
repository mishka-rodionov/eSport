package com.competra.remote.request.rating

import com.google.gson.annotations.SerializedName

data class GroupMappingEntry(
    @SerializedName("participantGroupId") val participantGroupId: Long,
    @SerializedName("ratingGroupId") val ratingGroupId: Long
)

data class SetGroupMappingRequest(
    @SerializedName("mappings") val mappings: List<GroupMappingEntry>
)
