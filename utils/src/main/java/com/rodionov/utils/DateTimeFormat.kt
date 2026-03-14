package com.rodionov.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Объект для работы с форматированием даты и времени.
 */
object DateTimeFormat {

    private val defaultFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * Форматирует LocalDate в строку dd.MM.yyyy.
     */
    fun formatDate(date: LocalDate): String = date.format(defaultFormatter)

    /**
     * Преобразует строку даты (yyyy-MM-dd) в Long (timestamp в миллисекундах).
     */
    fun transformApiDateToLong(date: String?): Long {
        if (date.isNullOrBlank()) return 0L
        return try {
            LocalDate.parse(date, defaultFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Преобразует Long (timestamp) в строку даты для API (yyyy-MM-dd).
     */
    fun transformLongToApiDate(date: Long?): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .format(apiFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Преобразует Long (timestamp) в пользовательскую строку даты (dd.MM.yyyy).
     */
    fun transformLongToDisplayDate(date: Long?): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(defaultFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Преобразует Long (timestamp) в строку времени (HH:mm).
     */
    fun transformLongToTime(date: Long?): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime()
                .format(timeFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Обновляет время в существующем timestamp или создает новый с текущей датой.
     *
     * @param timestamp Исходный timestamp.
     * @param timeString Строка времени в формате HH:mm.
     * @return Обновленный timestamp в миллисекундах.
     */
    fun updateTimeInTimestamp(timestamp: Long?, timeString: String): Long? {
        if (timeString.isBlank()) return null
        return try {
            val localTime = LocalTime.parse(timeString, timeFormatter)
            val baseDate = if (timestamp != null && timestamp != 0L) {
                java.time.Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            } else {
                LocalDate.now()
            }
            baseDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            timestamp
        }
    }

}
