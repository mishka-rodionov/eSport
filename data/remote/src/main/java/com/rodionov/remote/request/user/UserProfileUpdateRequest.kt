package com.rodionov.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserProfileUpdateRequest(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("middle_name") val middleName: String? = null,
    @SerializedName("birth_date") val birthDate: Long? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)
