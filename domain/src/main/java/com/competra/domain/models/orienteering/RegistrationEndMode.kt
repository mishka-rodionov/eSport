package com.competra.domain.models.orienteering

/**
 * Режим определения окончания регистрации на соревнование.
 *
 * @property DAY_BEFORE_START Регистрация закрывается за 24 часа до старта.
 * @property AT_COMPETITION_START Регистрация закрывается в момент старта соревнования;
 * статус соревнования автоматически меняется с [CompetitionStatus.REGISTRATION_OPEN]
 * на [CompetitionStatus.IN_PROGRESS].
 */
enum class RegistrationEndMode {
    DAY_BEFORE_START,
    AT_COMPETITION_START
}
