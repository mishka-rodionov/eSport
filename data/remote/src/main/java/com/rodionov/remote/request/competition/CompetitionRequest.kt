package com.rodionov.remote.request.competition

import com.google.gson.annotations.SerializedName

/**
 * Запрос на создание/обновление соревнования на сервере.
 */
data class CompetitionRequest(
    @SerializedName("remoteId")
    val remoteId: Long?,
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
    val mainOrganizerId: Long?,
    @SerializedName("coordinates")
    val coordinates: CoordinatesRequest?,
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
    @SerializedName("regulationUrl")
    val regulationUrl: String?,
    @SerializedName("mapUrl")
    val mapUrl: String?,
    @SerializedName("contactPhone")
    val contactPhone: String?,
    @SerializedName("contactEmail")
    val contactEmail: String?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("resultsStatus")
    val resultsStatus: String
)
