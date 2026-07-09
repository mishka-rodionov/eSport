package com.competra.domain.repository.clubs

import com.competra.domain.models.club.ClubJoinRequest

interface ClubJoinRequestRepository {

    /** Только если у клуба allowJoinRequests == true. */
    suspend fun create(clubId: String): Result<ClubJoinRequest>

    /** Входящие заявки клуба — только FOUNDER/ADMIN. */
    suspend fun listForClub(clubId: String): Result<List<ClubJoinRequest>>

    /** Собственные заявки текущего пользователя, во всех клубах. */
    suspend fun listMine(): Result<List<ClubJoinRequest>>

    suspend fun review(clubId: String, requestId: String, approve: Boolean): Result<ClubJoinRequest>
}
