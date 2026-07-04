package com.competra.remote.response.user

import com.google.gson.annotations.SerializedName
import com.competra.domain.models.Gender

/**
 * Ответ сервера с информацией о пользователе системы.
 *
 * @property id уникальный идентификатор пользователя.
 * @property firstName имя.
 * @property lastName фамилия.
 * @property middleName отчество. Может отсутствовать.
 * @property birthDate дата рождения в формате Long (миллисекунды).
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
    val middleName: String?,
    @SerializedName("birth_date")
    val birthDate: Long,
    @SerializedName("gender")
    val gender: Gender,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("avatar_crop_x")
    val avatarCropX: Double?,
    @SerializedName("avatar_crop_y")
    val avatarCropY: Double?,
    @SerializedName("avatar_crop_width")
    val avatarCropWidth: Double?,
    @SerializedName("avatar_crop_height")
    val avatarCropHeight: Double?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("qualification")
    val qualification: List<QualificationResponse>
)
