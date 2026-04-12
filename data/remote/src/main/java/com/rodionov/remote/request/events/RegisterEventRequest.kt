package com.rodionov.remote.request.events

import com.google.gson.annotations.SerializedName

/**
 * Запрос на регистрацию пользователя в группу соревнования.
 *
 * @property competitionId Идентификатор соревнования (remoteId).
 * @property groupId Идентификатор группы.
 * @property firstName Имя пользователя.
 * @property lastName Фамилия пользователя.
 */
data class RegisterEventRequest(
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("groupId")
    val groupId: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String
)
