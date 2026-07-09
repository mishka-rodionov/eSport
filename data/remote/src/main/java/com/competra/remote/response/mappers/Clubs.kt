package com.competra.remote.response.mappers

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.Club
import com.competra.domain.models.club.ClubJoinRequest
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.models.club.JoinRequestStatus
import com.competra.domain.models.club.Team
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole
import com.competra.remote.response.clubs.ClubJoinRequestResponse
import com.competra.remote.response.clubs.ClubMemberResponse
import com.competra.remote.response.clubs.ClubResponse
import com.competra.remote.response.clubs.TeamMemberResponse
import com.competra.remote.response.clubs.TeamResponse

fun ClubResponse.toDomain() = Club(
    id = id,
    name = name,
    description = description,
    allowJoinRequests = allowJoinRequests,
    foundedAt = foundedAt,
    membersCount = membersCount
)

fun ClubMemberResponse.toDomain() = ClubMember(
    id = id,
    clubId = clubId,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    role = ClubRole.valueOf(role),
    joinedAt = joinedAt
)

fun ClubJoinRequestResponse.toDomain() = ClubJoinRequest(
    id = id,
    clubId = clubId,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    status = JoinRequestStatus.valueOf(status),
    createdAt = createdAt
)

fun TeamResponse.toDomain() = Team(
    id = id,
    clubId = clubId,
    name = name,
    sportType = KindOfSport.fromName(sportType) ?: KindOfSport.Orienteering,
    membersCount = membersCount
)

fun TeamMemberResponse.toDomain() = TeamMember(
    id = id,
    teamId = teamId,
    clubMemberId = clubMemberId,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    role = TeamRole.valueOf(role)
)
