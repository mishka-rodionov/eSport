package com.rodionov.local.mappers

import com.rodionov.domain.models.user.User
import com.rodionov.local.entities.user.UserEntity

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

fun UserEntity.toDomain(): User