package com.rodionov.utils.orienteering

/**
 * Форматирует время из миллисекунд в спортивный формат H:MM:SS.
 *
 * Примеры:
 * 65000 -> 0:01:05
 * 3665000 -> 1:01:05
 */
fun Long.toRaceTime(): String {
    val totalSeconds = this / 1000

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%d:%02d:%02d".format(hours, minutes, seconds)
}

/**
 * Возвращает отставание от лидера.
 *
 * Если участник лидер — возвращает "—".
 */
fun Long.toGapFromLeader(leaderTime: Long): String {
    val gap = this - leaderTime

    if (gap <= 0) return "—"

    val totalSeconds = gap / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "+%d:%02d".format(minutes, seconds)
}

/**
 * Форматирует сплит (время между КП) в формате MM:SS.
 */
fun Long.toSplitTime(): String {

    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}