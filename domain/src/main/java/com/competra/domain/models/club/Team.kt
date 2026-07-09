package com.competra.domain.models.club

import com.competra.domain.models.KindOfSport

/** Команда внутри клуба. Клуб может иметь несколько команд одного вида спорта. */
data class Team(
    val id: String,
    val clubId: String,
    val name: String,
    val sportType: KindOfSport,
    val membersCount: Int
)
