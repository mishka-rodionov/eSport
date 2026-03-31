package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.Distance

/**
 * Интерфейс локального репозитория для работы с данными соревнований по ориентированию.
 */
interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<OrienteeringCompetition>
    suspend fun saveCompetitions(orienteeringCompetition: List<OrienteeringCompetition>): Result<List<OrienteeringCompetition>>
    suspend fun updateCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any>
    suspend fun getCompetition(competitionId: Long): Result<OrienteeringCompetition?>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any>
    suspend fun updateParticipantsGroups(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<Any>
    suspend fun updateParticipantGroup(participantGroup: ParticipantGroup): Result<Any>
    suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails>
    suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>>

    suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant?>

    suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>>
    suspend fun updateParticipants(participants: List<OrienteeringParticipant>) : Result<Any>

    suspend fun getParticipantByChipNumber(competitionId: Long, chipNumber: Int) : Result<OrienteeringParticipant>
    suspend fun getParticipantGroup(groupId: Long) : Result<ParticipantGroup>
    suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult): Result<Any>
    suspend fun getResultByParticipant(participantId: Long): Result<OrienteeringResult?>
    suspend fun getResultForGroup(competitionId: Long, groupId: Long): Result<List<OrienteeringResult>>
    suspend fun updateResults(orienteeringResult: List<OrienteeringResult>): Result<Any>

    suspend fun getResultByGroups(competitionId: Long): Result<List<GroupWithParticipantsAndResults>>

    /**
     * Обновляет флаг возможности редактирования для всех результатов соревнования.
     *
     * @param competitionId Идентификатор соревнования.
     * @param isEditable Значение флага.
     */
    suspend fun updateIsEditableForCompetition(competitionId: Long, isEditable: Boolean): Result<Any>

    /**
     * Сохраняет новую дистанцию для соревнования.
     * 
     * @param distance Модель дистанции.
     * @return Результат операции с ID сохраненной записи.
     */
    suspend fun saveDistance(distance: Distance): Result<Long>

    /**
     * Получает список дистанций соревнования.
     */
    suspend fun getDistances(competitionId: Long): Result<List<Distance>>

    /**
     * Обновляет данные существующей дистанции.
     * 
     * @param distance Модель дистанции.
     * @return Результат операции.
     */
    suspend fun updateDistance(distance: Distance): Result<Any>
}
