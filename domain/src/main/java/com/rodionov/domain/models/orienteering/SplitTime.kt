package com.rodionov.domain.models.orienteering

/**
 * Представляет отметку участника на контрольном пункте дистанции.
 *
 * @property controlPoint идентификатор или название контрольного пункта.
 * @property timestamp время отметки в миллисекундах с начала эпохи Unix (UTC).
 */
data class SplitTime(
    val controlPoint: String,
    val timestamp: Long
)
