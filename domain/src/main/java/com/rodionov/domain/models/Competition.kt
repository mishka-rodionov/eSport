package com.rodionov.domain.models

import androidx.room.Embedded

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
data class Competition(
    val title: String,
    val date: Long,
    val kindOfSport: KindOfSport,
    val description: String,
    val address: String,
    val mainOrganizer: String,
    @Embedded
    val coordinates: Coordinates,
)
