package com.competra.domain.models.user

import com.competra.domain.models.Gender
import com.competra.domain.models.Qualification
import java.util.Date

/**
 * Представляет модель данных пользователя в системе.
 *
 * @property id Уникальный идентификатор пользователя.
 * @property firstName Имя пользователя.
 * @property lastName Фамилия пользователя.
 * @property middleName Отчество пользователя (может отсутствовать).
 * @property birthDate Дата рождения пользователя (в миллисекундах).
 * @property gender Пол пользователя.
 * @property photo Ссылка на фотографию профиля пользователя.
 * @property phoneNumber Номер телефона пользователя.
 * @property email Адрес электронной почты пользователя.
 * @property qualification Список спортивных квалификаций пользователя.
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val birthDate: Long,
    val gender: Gender,
    val avatarUrl: String,
    val phoneNumber: String?,
    val email: String,
    val qualification: List<Qualification>
)
