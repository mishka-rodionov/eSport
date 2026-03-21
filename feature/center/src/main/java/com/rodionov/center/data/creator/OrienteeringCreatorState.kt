package com.rodionov.center.data.creator

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.ResultsStatus
import com.rodionov.ui.BaseState

/**
 * Состояние экрана создания соревнования.
 * 
 * @property competitionId ID соревнования (null если создание)
 * @property title Заголовок
 * @property date Дата (millis)
 * @property time Время (строка)
 * @property address Адрес
 * @property description Описание
 * @property participantGroups Список групп
 * @property errors Ошибки валидации
 * @property isShowGroupCreateDialog Показ диалога создания группы
 * @property punchingSystem Система отметки
 * @property editGroupIndex Индекс редактируемой группы
 * @property competitionDirection Направление
 * @property startTimeMode Режим старта
 * @property countdownTimer Таймер отсчета (мин)
 */
data class OrienteeringCreatorState(
    val competitionId: Long? = null,
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val time: String = "12:00",
    val address: String = "",
    val description: String = "",
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val errors: OrienteeringCreatorErrors = OrienteeringCreatorErrors(),
    val isShowGroupCreateDialog: Boolean = false,
    val punchingSystem: PunchingSystem = PunchingSystem.SPORTIDUINO,
    val editGroupIndex: Int = -1,
    val competitionDirection: OrienteeringDirection? = null,
    val startTimeMode: StartTimeMode = StartTimeMode.STRICT,
    val countdownTimer: Int? = null
) : BaseState {
    fun constructOrienteeringCompetition(userId: String): OrienteeringCompetition {
        return OrienteeringCompetition(
            competitionId = competitionId ?: (-9999..-1000).random().toLong(),
            competition = Competition(
                remoteId = null,
                title = title,
                startDate = date,
                endDate = date + 86400000L, // +1 день по умолчанию
                kindOfSport = KindOfSport.Orienteering,
                description = description,
                address = address,
                mainOrganizerId = userId.toLongOrNull(),
                coordinates = Coordinates(0.0, 0.0),
                status = CompetitionStatus.DRAFT,
                registrationStart = System.currentTimeMillis(),
                registrationEnd = date - 3600000L,
                maxParticipants = 500,
                feeAmount = 0.0,
                feeCurrency = "RUB",
                resultsStatus = ResultsStatus.NOT_PUBLISHED
            ),
            direction = competitionDirection ?: OrienteeringDirection.FORWARD,
            punchingSystem = punchingSystem,
            startTimeMode = startTimeMode,
            countdownTimer = countdownTimer
        )
    }
}
