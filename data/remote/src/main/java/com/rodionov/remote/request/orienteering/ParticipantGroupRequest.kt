package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName

/**
 * Запрос на создание или обновление группы участников соревнования.
 *
 * @property groupId идентификатор группы. Может быть `null` при создании новой.
 * @property competitionId идентификатор соревнования.
 * @property title название группы.
 * @property distance длина дистанции в километрах.
 * @property countOfControls количество контрольных пунктов.
 * @property maxTimeInMinute контрольное время в минутах.
 * @property controlPoints набор контрольных пунктов для группы.
 */
data class ParticipantGroupRequest(
    @SerializedName("groupId")
    val groupId: Long? = null, //может быть null при создании группы участников
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
    val controlPoints: List<ControlPointRequest>
)

