package com.competra.app.service

/**
 * Событие предстартового оповещения участников.
 *
 * В режиме жеребьевки по дистанциям несколько участников (по одному с каждой дистанции)
 * стартуют в один и тот же момент, поэтому оповещение оперирует списком стартующих,
 * а не одним участником.
 */
sealed class ParticipantStartAlert {

    /** Краткие данные одного стартующего участника. */
    data class Starter(
        val participantName: String,
        val startNumber: String
    )

    /** Участники стартуют через [countdownSeconds] секунд (1..10). */
    data class Upcoming(
        val starters: List<Starter>,
        val countdownSeconds: Int,
        val nextStarters: List<Starter>
    ) : ParticipantStartAlert()

    /** Участники только что стартовали. */
    data class Started(
        val starters: List<Starter>
    ) : ParticipantStartAlert()
}
