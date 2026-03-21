package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName

/**
 * Запрос на создание или обновление группы участников соревнования.
 *
 * @property groupId Идентификатор группы (может быть null при создании).
 * @property competitionId Идентификатор соревнования.
 * @property title Название группы (например, "М21").
 * @property gender Пол участников (MALE, FEMALE, MIXED).
 * @property minAge Минимальный возраст.
 * @property maxAge Максимальный возраст.
 * @property distanceId Идентификатор связанной дистанции.
 * @property maxParticipants Лимит участников для группы.
 */
data class ParticipantGroupRequest(
    @SerializedName("groupId")
    val groupId: Long? = null,
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
    val maxParticipants: Int?
)
