package com.rodionov.remote.request.user

import com.google.gson.annotations.SerializedName

/**
 * Модель запроса для создания или обновления пользователя.
 *
 * @property firstName Имя пользователя.
 * @property lastName Фамилия пользователя.
 * @property birthDate Дата рождения пользователя (в миллисекундах).
 * @property email Электронная почта пользователя.
 */
data class UserRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("birth_date")
    val birthDate: Long,
    @SerializedName("email")
    val email: String
)
