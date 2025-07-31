package com.rodionov.local.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.local.entities.CompetitionEntity

fun CompetitionEntity.toDomain(): Competition = Competition(
    id = id,
    title = title,
    date = date,
    kindOfSport = kindOfSport,
    description = description,
    address = address,
    coordinates = coordinates
)

fun Competition.toEntity(): CompetitionEntity = CompetitionEntity(
    id = id,
    title = title,
    date = date,
    kindOfSport = kindOfSport,
    description = description,
    address = address,
    coordinates = coordinates
)