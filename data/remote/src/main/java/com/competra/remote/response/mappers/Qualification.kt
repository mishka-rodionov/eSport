package com.competra.remote.response.mappers

import com.competra.domain.models.Qualification
import com.competra.remote.response.user.QualificationResponse

fun QualificationResponse.toDomain(): Qualification {
    return Qualification(
        kindOfSport = kindOfSport,
        sportsCategory = sportsCategory
    )
}