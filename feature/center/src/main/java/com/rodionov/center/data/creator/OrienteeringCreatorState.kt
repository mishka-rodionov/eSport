package com.rodionov.center.data.creator

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.*
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.ResultsStatus
import com.rodionov.ui.BaseState

/**
 * Состояние процесса создания соревнования.
 * 
 * @property competitionId Идентификатор соревнования (локальный).
 * @property title Название.
 * @property startDate Дата начала.
 * @property startTimeStr Время начала (строка HH:mm).
 * @property endDate Дата окончания.
 * @property kindOfSport Вид спорта.
 * @property description Описание.
 * @property address Адрес.
 * @property coordinates Координаты.
 * @property registrationStart Начало регистрации.
 * @property registrationEnd Конец регистрации.
 * @property maxParticipants Лимит участников.
 * @property isFeeEnabled Флаг активности поля взноса.
 * @property feeAmount Сумма взноса.
 * @property feeCurrency Валюта взноса.
 * @property regulationUrl Ссылка на регламент.
 * @property mapUrl Ссылка на карту.
 * @property contactPhone Телефон.
 * @property contactEmail Почта.
 * @property website Сайт.
 * @property competitionDirection Направление.
 * @property punchingSystem Система отметки.
 * @property startTimeMode Режим старта.
 * @property countdownTimer Таймер.
 * @property editGroupIndex Индекс редактируемой группы.
 * @property isShowGroupCreateDialog Флаг отображения диалога создания группы.
 * @property editDistanceIndex Индекс редактируемой дистанции.
 * @property isShowDistanceCreateDialog Флаг отображения диалога создания дистанции.
 * @property errors Ошибки валидации.
 * @property distances Список дистанций.
 * @property participantGroups Список групп.
 * @property stages Список этапов (для многодневных соревнований).
 * @property isLoading Флаг загрузки.
 * @property error Сообщение об ошибке.
 */
data class OrienteeringCreatorState(
    val competitionId: Long? = null,
    val title: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val startTimeStr: String = "10:00",
    val endDate: Long? = null,
    val kindOfSport: KindOfSport = KindOfSport.Orienteering,
    val description: String = "",
    val address: String = "",
    val coordinates: Coordinates = Coordinates(0.0, 0.0),
    
    val registrationStart: Long? = null,
    val registrationStartTimeStr: String = "10:00",
    val registrationStartOnCreate: Boolean = false,
    val registrationEnd: Long? = null,
    val registrationEndTimeStr: String = "23:59",
    val registrationEndDayBefore: Boolean = false,
    val maxParticipants: Int? = null,
    
    val isFeeEnabled: Boolean = false,
    val feeAmount: Double? = null,
    val feeCurrency: String = "RUB",
    val regulationUrl: String = "",
    
    val mapUrl: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val website: String = "",

    val competitionDirection: OrienteeringDirection = OrienteeringDirection.FORWARD,
    val punchingSystem: PunchingSystem = PunchingSystem.SPORTIDUINO,
    val startTimeMode: StartTimeMode = StartTimeMode.STRICT,
    val countdownTimer: Int? = null,

    val editGroupIndex: Int = -1,
    val isShowGroupCreateDialog: Boolean = false,
    val editDistanceIndex: Int = -1,
    val isShowDistanceCreateDialog: Boolean = false,
    val errors: OrienteeringCreatorErrors = OrienteeringCreatorErrors(),
    
    val distances: List<Distance> = emptyList(),
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val stages: List<Stage> = emptyList(),
    
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseState {
    
    fun toOrienteeringCompetition(userId: String?): OrienteeringCompetition {
        return OrienteeringCompetition(
            localCompetitionId = competitionId ?: 0L,
            competition = Competition(
                title = title,
                startDate = startDate,
                endDate = endDate,
                kindOfSport = kindOfSport,
                description = description,
                address = address,
                mainOrganizerId = userId,
                coordinates = coordinates,
                status = CompetitionStatus.DRAFT,
                registrationStart = registrationStart,
                registrationEnd = registrationEnd,
                maxParticipants = maxParticipants,
                feeAmount = if (isFeeEnabled) feeAmount else null,
                feeCurrency = feeCurrency,
                regulationUrl = regulationUrl,
                mapUrl = mapUrl,
                contactPhone = contactPhone,
                contactEmail = contactEmail,
                website = website,
                resultsStatus = ResultsStatus.NOT_PUBLISHED
            ),
            direction = competitionDirection,
            punchingSystem = punchingSystem,
            startTimeMode = startTimeMode,
            countdownTimer = countdownTimer
        )
    }
}
