package com.competra.remote.repository.clubs

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.Team
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole
import com.competra.domain.repository.clubs.TeamRepository
import com.competra.remote.datasource.clubs.TeamRemoteDataSource
import com.competra.remote.request.clubs.AddTeamMemberRequest
import com.competra.remote.request.clubs.ChangeTeamMemberRoleRequest
import com.competra.remote.request.clubs.CreateTeamRequest
import com.competra.remote.request.clubs.UpdateTeamRequest
import com.competra.remote.response.mappers.toDomain

class TeamRepositoryImpl(
    private val teamRemoteDataSource: TeamRemoteDataSource
) : TeamRepository {

    override suspend fun listByClub(clubId: String): Result<List<Team>> {
        return teamRemoteDataSource.listByClub(clubId).mapCatching { it.result!!.map { team -> team.toDomain() } }
    }

    override suspend fun getById(teamId: String): Result<Team> {
        return teamRemoteDataSource.getById(teamId).mapCatching { it.result!!.toDomain() }
    }

    override suspend fun create(clubId: String, name: String, sportType: KindOfSport): Result<Team> {
        return teamRemoteDataSource.create(clubId, CreateTeamRequest(name, sportType.name))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun update(teamId: String, name: String, sportType: KindOfSport): Result<Team> {
        return teamRemoteDataSource.update(teamId, UpdateTeamRequest(name, sportType.name))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun delete(teamId: String): Result<Unit> {
        return teamRemoteDataSource.delete(teamId).mapCatching { Unit }
    }

    override suspend fun getMembers(teamId: String): Result<List<TeamMember>> {
        return teamRemoteDataSource.getMembers(teamId).mapCatching { it.result!!.map { member -> member.toDomain() } }
    }

    override suspend fun addMember(teamId: String, clubMemberId: String, role: TeamRole): Result<TeamMember> {
        return teamRemoteDataSource.addMember(teamId, AddTeamMemberRequest(clubMemberId, role.name))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun removeMember(teamId: String, clubMemberId: String): Result<Unit> {
        return teamRemoteDataSource.removeMember(teamId, clubMemberId).mapCatching { Unit }
    }

    override suspend fun changeMemberRole(teamId: String, clubMemberId: String, role: TeamRole): Result<TeamMember> {
        return teamRemoteDataSource.changeMemberRole(teamId, clubMemberId, ChangeTeamMemberRoleRequest(role.name))
            .mapCatching { it.result!!.toDomain() }
    }
}
