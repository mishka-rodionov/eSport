package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Qualification
import com.rodionov.remote.response.user.QualificationResponse

fun QualificationResponse.toDomain(): Qualification {
    return Qualification(
        kindOfSport = kindOfSport,
        sportsCategory = sportsCategory
    )
}