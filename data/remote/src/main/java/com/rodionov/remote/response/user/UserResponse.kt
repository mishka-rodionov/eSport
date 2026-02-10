package com.rodionov.remote.response.user

import com.google.gson.annotations.SerializedName
import com.rodionov.domain.models.Gender

/**
 * Ответ сервера с информацией о пользователе системы.
 *
 * Используется при авторизации, получении профиля и других операциях,
 * связанных с персональными данными пользователя.
 *
 * @property id уникальный идентификатор пользователя.
 * @property firstName имя.
 * @property lastName фамилия.
 * @property middleName отчество. Может отсутствовать.
 * @property birthDate дата рождения в строковом формате,
 * определённом контрактом API.
 * @property gender пол пользователя.
 * @property photo ссылка на фотографию профиля.
 * @property phoneNumber номер телефона. Может отсутствовать.
 * @property email адрес электронной почты.
 * @property qualification список спортивных квалификаций пользователя.
 */
data class UserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("middle_name")
    val middleName: String?, // отчество
    @SerializedName("birth_date")
    val birthDate: String,
    @SerializedName("gender")
    val gender: Gender,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("qualification")
    val qualification: List<QualificationResponse>
)
