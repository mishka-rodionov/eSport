package com.competra.domain.repository.clubs

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.Team
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole

interface TeamRepository {

    suspend fun listByClub(clubId: String): Result<List<Team>>

    suspend fun getById(teamId: String): Result<Team>

    suspend fun create(clubId: String, name: String, sportType: KindOfSport): Result<Team>

    suspend fun update(teamId: String, name: String, sportType: KindOfSport): Result<Team>

    suspend fun delete(teamId: String): Result<Unit>

    suspend fun getMembers(teamId: String): Result<List<TeamMember>>

    suspend fun addMember(teamId: String, clubMemberId: String, role: TeamRole): Result<TeamMember>

    suspend fun removeMember(teamId: String, clubMemberId: String): Result<Unit>

    suspend fun changeMemberRole(teamId: String, clubMemberId: String, role: TeamRole): Result<TeamMember>
}
