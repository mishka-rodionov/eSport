package com.rodionov.domain.models

import androidx.room.Embedded
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.ResultsStatus

/**
 * Базовая модель соревнования.
 *
 * Представляет общую информацию о спортивном событии, включая его сроки, место проведения,
 * организаторов, условия участия и статус синхронизации с сервером.
 *
 * @property remoteId Идентификатор соревнования на сервере (null, если запись еще не синхронизирована).
 * @property title Название соревнования.
 * @property startDate Дата и время начала соревнования (Unix timestamp в мс).
 * @property endDate Дата и время окончания соревнования (Unix timestamp в мс).
 * @property kindOfSport Вид спорта (например, ориентирование, лыжные гонки).
 * @property description Подробное описание мероприятия.
 * @property address Физический адрес или описание места проведения.
 * @property mainOrganizerId Локальный идентификатор главного организатора (ссылка на пользователя).
 * @property coordinates Географические координаты места проведения (широта и долгота).
 * @property status Текущий статус жизненного цикла соревнования (Черновик, Регистрация, Завершено и т.д.).
 * @property registrationStart Дата и время начала регистрации участников (Unix timestamp в мс).
 * @property registrationEnd Дата и время окончания регистрации участников (Unix timestamp в мс).
 * @property maxParticipants Максимально допустимое количество участников.
 * @property feeAmount Стоимость участия (взнос).
 * @property feeCurrency Валюта оплаты взноса.
 * @property regulationUrl Ссылка на документ с положением или регламентом соревнований.
 * @property mapUrl Ссылка на карту района соревнований или схему проезда.
 * @property contactPhone Контактный номер телефона организаторов.
 * @property contactEmail Контактный адрес электронной почты организаторов.
 * @property website Официальный сайт или страница соревнований.
 * @property resultsStatus Статус публикации результатов (Предварительные, Официальные и т.д.).
 * @property isSynced Флаг успешной синхронизации локальной записи с сервером.
 * @property lastModified Время последнего изменения записи (Unix timestamp в мс).
 * @property isDeleted Флаг "мягкого" удаления (запись помечена как удаленная для последующей синхронизации).
 * @property createdAt Время создания записи в локальной базе данных (Unix timestamp в мс).
 * @property syncError Текст последней ошибки синхронизации, если она произошла.
 */
data class Competition(
    val remoteId: String? = null,
    val title: String,
    val startDate: Long,
    val endDate: Long? = null,
    val kindOfSport: KindOfSport,
    val description: String? = null,
    val address: String? = null,
    val mainOrganizerId: String? = null,          // ссылка на пользователя (локальный ID)
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
