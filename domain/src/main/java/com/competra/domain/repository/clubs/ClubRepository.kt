package com.competra.domain.repository.clubs

import com.competra.domain.models.PagedResult
import com.competra.domain.models.club.Club
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole

/** Клубы работают только online — без offline-first/`:core:sync`, ошибки сети — обычный [Result.failure]. */
interface ClubRepository {

    suspend fun search(query: String?, page: Int, limit: Int): Result<PagedResult<Club>>

    /** Клубы, где текущий пользователь FOUNDER/ADMIN — для выбора клуба-организатора соревнования. */
    suspend fun listMine(): Result<List<Club>>

    suspend fun getById(clubId: String): Result<Club>

    suspend fun create(name: String, description: String?, allowJoinRequests: Boolean): Result<Club>

    suspend fun update(clubId: String, name: String, description: String?, allowJoinRequests: Boolean): Result<Club>

    /** Только FOUNDER. Соревнования, созданные от лица клуба, не удаляются — остаются без владельца-клуба. */
    suspend fun delete(clubId: String): Result<Unit>

    suspend fun getMembers(clubId: String): Result<List<ClubMember>>

    /** targetUserId == текущий пользователь — самостоятельный выход из клуба. */
    suspend fun removeMember(clubId: String, targetUserId: String): Result<Unit>

    /** [ClubRole.FOUNDER] в качестве новой роли — передача роли основателя. */
    suspend fun changeMemberRole(clubId: String, targetUserId: String, role: ClubRole): Result<ClubMember>
}
