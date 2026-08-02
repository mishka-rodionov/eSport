package com.competra.remote.response.mappers

import com.competra.domain.models.CropRect
import com.competra.domain.models.user.User
import com.competra.remote.response.user.QualificationResponse
import com.competra.remote.response.user.UserResponse

/**
 * Маппер для преобразования ответа сервера в доменную модель пользователя.
 */
fun UserResponse.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        middleName = middleName,
        birthDate = birthDate,
        gender = gender,
        avatarUrl = avatarUrl,
        phoneNumber = phoneNumber,
        email = email,
        qualification = qualification.map(QualificationResponse::toDomain),
        avatarCropRect = toAvatarCropRect()
    )
}

private fun UserResponse.toAvatarCropRect(): CropRect? {
    val x = avatarCropX ?: return null
    val y = avatarCropY ?: return null
    val width = avatarCropWidth ?: return null
    val height = avatarCropHeight ?: return null
    return CropRect(x, y, width, height)
}
