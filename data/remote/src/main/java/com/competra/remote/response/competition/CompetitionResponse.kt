package com.competra.remote.response.competition

import com.google.gson.annotations.SerializedName

/**
 * Ответ сервера с данными о соревновании.
 */
data class CompetitionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("legacyId")
    val legacyId: Long? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("startDate")
    val startDate: Long,
    @SerializedName("endDate")
    val endDate: Long?,
    @SerializedName("kindOfSport")
    val kindOfSport: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("mainOrganizerId")
    val mainOrganizerId: String?,
    @SerializedName("coordinates")
    val coordinates: CoordinatesResponse?,
    @SerializedName("status")
    val status: String,
    @SerializedName("registrationStart")
    val registrationStart: Long?,
    @SerializedName("registrationEnd")
    val registrationEnd: Long?,
    @SerializedName("maxParticipants")
    val maxParticipants: Int?,
    @SerializedName("feeAmount")
    val feeAmount: Double?,
    @SerializedName("feeCurrency")
    val feeCurrency: String?,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("regulationUrl")
    val regulationUrl: String?,
    @SerializedName("mapUrl")
    val mapUrl: String?,
    @SerializedName("resultsUrl")
    val resultsUrl: String? = null,
    @SerializedName("contactPhone")
    val contactPhone: String?,
    @SerializedName("contactEmail")
    val contactEmail: String?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("resultsStatus")
    val resultsStatus: String,
    @SerializedName("timeZoneId")
    val timeZoneId: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: Long = 0L
)
