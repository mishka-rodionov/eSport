package com.competra.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Объект для работы с форматированием даты и времени.
 *
 * Перегрузки с явным `zoneId` нужны для соревнований: время соревнования
 * (старт, регистрация) должно отображаться в часовом поясе соревнования,
 * а не в системной зоне устройства просматривающего.
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

    fun transformLongToDisplayDate(date: Long?, zoneId: ZoneId): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(zoneId)
                .toLocalDate()
                .format(defaultFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    fun transformLongToDisplayDate(date: Long?): String =
        transformLongToDisplayDate(date, ZoneId.systemDefault())

    fun transformLongToTime(date: Long?, zoneId: ZoneId): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(zoneId)
                .toLocalTime()
                .format(timeFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    fun transformLongToTime(date: Long?): String =
        transformLongToTime(date, ZoneId.systemDefault())

    /**
     * Обновляет время в существующем timestamp или создает новый с текущей датой.
     * Дата/время интерпретируются в указанном [zoneId] — это позволяет вводить
     * локальное время соревнования и получать корректный UTC-epoch.
     */
    fun updateTimeInTimestamp(timestamp: Long?, timeString: String, zoneId: ZoneId): Long? {
        if (timeString.isBlank()) return null
        return try {
            val localTime = LocalTime.parse(timeString, timeFormatter)
            val baseDate = if (timestamp != null && timestamp != 0L) {
                java.time.Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()
            } else {
                LocalDate.now(zoneId)
            }
            baseDate.atTime(localTime).atZone(zoneId).toInstant().toEpochMilli()
        } catch (e: Exception) {
            timestamp
        }
    }

    fun updateTimeInTimestamp(timestamp: Long?, timeString: String): Long? =
        updateTimeInTimestamp(timestamp, timeString, ZoneId.systemDefault())

}
