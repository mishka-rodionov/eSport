package com.competra.remote.repository.clubs

import com.competra.domain.models.PagedResult
import com.competra.domain.models.club.Club
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.remote.datasource.clubs.ClubRemoteDataSource
import com.competra.remote.request.clubs.ChangeRoleRequest
import com.competra.remote.request.clubs.CreateClubRequest
import com.competra.remote.request.clubs.UpdateClubRequest
import com.competra.remote.response.mappers.toDomain

class ClubRepositoryImpl(
    private val clubRemoteDataSource: ClubRemoteDataSource
) : ClubRepository {

    override suspend fun search(query: String?, page: Int, limit: Int): Result<PagedResult<Club>> {
        return clubRemoteDataSource.search(query, page, limit).mapCatching { response ->
            val paged = response.result
            PagedResult(
                items = paged?.items.orEmpty().map { it.toDomain() },
                hasMore = paged?.hasMore ?: false
            )
        }
    }

    override suspend fun getById(clubId: String): Result<Club> {
        return clubRemoteDataSource.getById(clubId).mapCatching { it.result!!.toDomain() }
    }

    override suspend fun listMine(): Result<List<Club>> {
        return clubRemoteDataSource.listMine().mapCatching { it.result!!.map { club -> club.toDomain() } }
    }

    override suspend fun create(name: String, description: String?, allowJoinRequests: Boolean): Result<Club> {
        return clubRemoteDataSource.create(CreateClubRequest(name, description, allowJoinRequests))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun update(
        clubId: String,
        name: String,
        description: String?,
        allowJoinRequests: Boolean
    ): Result<Club> {
        return clubRemoteDataSource.update(clubId, UpdateClubRequest(name, description, allowJoinRequests))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun delete(clubId: String): Result<Unit> {
        return clubRemoteDataSource.delete(clubId).mapCatching { Unit }
    }

    override suspend fun getMembers(clubId: String): Result<List<ClubMember>> {
        return clubRemoteDataSource.getMembers(clubId).mapCatching { it.result!!.map { member -> member.toDomain() } }
    }

    override suspend fun removeMember(clubId: String, targetUserId: String): Result<Unit> {
        return clubRemoteDataSource.removeMember(clubId, targetUserId).mapCatching { Unit }
    }

    override suspend fun changeMemberRole(clubId: String, targetUserId: String, role: ClubRole): Result<ClubMember> {
        return clubRemoteDataSource.changeMemberRole(clubId, targetUserId, ChangeRoleRequest(role.name))
            .mapCatching { it.result!!.toDomain() }
    }
}
