package com.competra.local.entities.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.competra.domain.models.Gender
import com.competra.domain.models.Qualification
import com.competra.local.converters.UserConverter

/**
 * Сущность пользователя для хранения в локальной базе данных Room.
 *
 * @property id Уникальный идентификатор пользователя.
 * @property firstName Имя.
 * @property lastName Фамилия.
 * @property middleName Отчество (может быть null).
 * @property birthDate Дата рождения в формате Long (timestamp).
 * @property gender Пол.
 * @property photo Ссылка или путь к фото.
 * @property phoneNumber Номер телефона.
 * @property email Электронная почта.
 * @property qualification Список квалификаций.
 */
@Entity(tableName = "users")
@TypeConverters(UserConverter::class)
data class UserEntity(
    @PrimaryKey
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
