package com.rodionov.sportsenthusiast.service

/**
 * Событие предстартового оповещения участника.
 */
sealed class ParticipantStartAlert {

    /** Участник стартует через [countdownSeconds] секунд (0..10). */
    data class Upcoming(
        val participantName: String,
        val startNumber: String,
        val countdownSeconds: Int,
        val nextParticipantName: String?,
        val nextStartNumber: String?
    ) : ParticipantStartAlert()

    /** Участник только что стартовал. */
    data class Started(
        val participantName: String,
        val startNumber: String
    ) : ParticipantStartAlert()
}
