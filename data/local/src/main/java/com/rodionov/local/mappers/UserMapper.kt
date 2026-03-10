package com.rodionov.local.mappers

import com.rodionov.domain.models.user.User
import com.rodionov.local.entities.user.UserEntity

/**
 * Маппер для преобразования доменной модели пользователя в сущность базы данных.
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        middleName = middleName,
        birthDate = birthDate,
        gender = gender,
        photo = photo,
        phoneNumber = phoneNumber,
        email = email,
        qualification = qualification
    )
}

/**
 * Маппер для преобразования сущности базы данных в доменную модель пользователя.
 */
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        middleName = middleName,
        birthDate = birthDate,
        gender = gender,
        photo = photo,
        phoneNumber = phoneNumber,
        email = email,
        qualification = qualification
    )
}
