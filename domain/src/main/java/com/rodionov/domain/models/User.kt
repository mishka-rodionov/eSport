package com.rodionov.domain.models

import java.util.Date

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?, // отчество
    val birthDate: Date,
    val gender: Gender,
    val photo: String,
    val phoneNumber: String?,
    val email: String,
    val qualification: Qualification
)
