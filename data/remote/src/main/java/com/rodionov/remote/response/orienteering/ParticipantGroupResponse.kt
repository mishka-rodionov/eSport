package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName

/**
 * Ответ сервера с информацией о группе участников соревнования.
 *
 * @property groupId ID группы на сервере.
 * @property competitionId ID соревнования на сервере.
 * @property title Название группы.
 * @property gender Пол участников.
 * @property minAge Минимальный возраст.
 * @property maxAge Максимальный возраст.
 * @property distanceId ID связанной дистанции.
 * @property maxParticipants Лимит участников для группы.
 */
data class ParticipantGroupResponse(
    @SerializedName("groupId")
    val groupId: Long,
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("minAge")
    val minAge: Int?,
    @SerializedName("maxAge")
    val maxAge: Int?,
    @SerializedName("distanceId")
    val distanceId: Long,
    @SerializedName("maxParticipants")
    val maxParticipants: Int?,
    @SerializedName("updatedAt")
    val updatedAt: Long = 0L
)
