package com.rodionov.domain.models.orienteering

/**
 * Класс с перечислением режимов начала соревнования
 * @property [STRICT] - режим строгого времени начала, соответствует указанному времени при регистрации соревнования
 * @property [USER_SET] - режим когда время начала можно задать пользователем непосредственно перед стартом
 * @property [BY_START_STATION] - режим когда время начала определяется по отметке на стартовой станции
 * */
enum class StartTimeMode {
    STRICT, USER_SET, BY_START_STATION
}