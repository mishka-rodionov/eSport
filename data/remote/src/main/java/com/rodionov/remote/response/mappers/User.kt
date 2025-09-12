package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.user.User
import com.rodionov.remote.request.user.UserRequest
import com.rodionov.remote.response.user.QualificationResponse
import com.rodionov.remote.response.user.UserResponse

fun User.toRequest(): UserRequest {
    return UserRequest(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        email = email
    )
}

fun UserResponse.toDomain(): User {
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
        qualification = qualification.map ( QualificationResponse::toDomain)
    )
}