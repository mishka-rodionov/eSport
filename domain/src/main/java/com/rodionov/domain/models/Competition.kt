package com.rodionov.domain.models

import androidx.room.Embedded
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.ResultsStatus

/**
 * Базовая модель соревнования.
 *
 * @property title Название соревнования.
 * @property date Дата проведения соревнования (в миллисекундах).
 * @property kindOfSport Вид спорта.
 * @property description Описание соревнования.
 * @property address Место проведения.
 * @property mainOrganizer Главный организатор (ID пользователя).
 * @property coordinates Географические координаты места проведения.
 */
//data class Competition(
//    val title: String,
//    val date: Long,
//    val kindOfSport: KindOfSport,
//    val description: String,
//    val address: String,
//    val mainOrganizer: String,
//    @Embedded
//    val coordinates: Coordinates,
//)

data class Competition(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
    val remoteId: Long? = null,
    val title: String,
    val startDate: Long,
    val endDate: Long? = null,
    val kindOfSport: KindOfSport,
    val description: String? = null,
    val address: String? = null,
    val mainOrganizerId: Long? = null,          // ссылка на пользователя (локальный ID)
    @Embedded
    val coordinates: Coordinates? = null,
    val status: CompetitionStatus,
    val registrationStart: Long? = null,
    val registrationEnd: Long? = null,
    val maxParticipants: Int? = null,
    val feeAmount: Double? = null,
    val feeCurrency: String? = null,
    val regulationUrl: String? = null,
    val mapUrl: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val website: String? = null,
    val resultsStatus: ResultsStatus,            // PRELIMINARY, OFFICIAL, NOT_PUBLISHED
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val syncError: String? = null
)
