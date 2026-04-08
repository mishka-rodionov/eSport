package com.rodionov.remote.request.events

import com.google.gson.annotations.SerializedName

/**
 * Запрос на регистрацию пользователя в группу события.
 *
 * @property eventId Идентификатор события.
 * @property groupId Идентификатор группы.
 */
data class RegisterEventRequest(
    @SerializedName("eventId")
    val eventId: String,
    @SerializedName("groupId")
    val groupId: String
)
