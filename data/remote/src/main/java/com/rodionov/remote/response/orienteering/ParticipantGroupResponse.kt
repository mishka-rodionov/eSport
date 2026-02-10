package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName

/**
 * Ответ сервера с информацией о группе участников соревнования.
 *
 * @property groupId уникальный идентификатор группы.
 * @property competitionId идентификатор соревнования, к которому относится группа.
 * @property title название группы (например, М21, Ж18 и т.п.).
 * @property distance длина дистанции в километрах.
 * @property countOfControls количество контрольных пунктов на дистанции.
 * @property maxTimeInMinute контрольное время прохождения дистанции в минутах.
 * @property controlPoints список контрольных пунктов, назначенных для группы.
 */
data class ParticipantGroupResponse(
    @SerializedName("groupId")
    val groupId: Long,
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("countOfControls")
    val countOfControls: Int,
    @SerializedName("maxTimeInMinute")
    val maxTimeInMinute: Int,
    @SerializedName("controlPoints")
    val controlPoints: List<ControlPointResponse>
)

