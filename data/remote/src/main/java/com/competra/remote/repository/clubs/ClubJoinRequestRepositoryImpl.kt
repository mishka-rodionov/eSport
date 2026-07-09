package com.competra.remote.repository.clubs

import com.competra.domain.models.club.ClubJoinRequest
import com.competra.domain.repository.clubs.ClubJoinRequestRepository
import com.competra.remote.datasource.clubs.ClubRemoteDataSource
import com.competra.remote.request.clubs.ReviewJoinRequestRequest
import com.competra.remote.response.mappers.toDomain

class ClubJoinRequestRepositoryImpl(
    private val clubRemoteDataSource: ClubRemoteDataSource
) : ClubJoinRequestRepository {

    override suspend fun create(clubId: String): Result<ClubJoinRequest> {
        return clubRemoteDataSource.createJoinRequest(clubId).mapCatching { it.result!!.toDomain() }
    }

    override suspend fun listForClub(clubId: String): Result<List<ClubJoinRequest>> {
        return clubRemoteDataSource.listJoinRequestsForClub(clubId)
            .mapCatching { it.result!!.map { request -> request.toDomain() } }
    }

    override suspend fun listMine(): Result<List<ClubJoinRequest>> {
        return clubRemoteDataSource.listMyJoinRequests()
            .mapCatching { it.result!!.map { request -> request.toDomain() } }
    }

    override suspend fun review(clubId: String, requestId: String, approve: Boolean): Result<ClubJoinRequest> {
        return clubRemoteDataSource.reviewJoinRequest(clubId, requestId, ReviewJoinRequestRequest(approve))
            .mapCatching { it.result!!.toDomain() }
    }
}
