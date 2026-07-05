package com.competra.diary.presentation.common

import com.competra.domain.diary.TrackPoint
import com.competra.domain.models.diary.SportType
import java.time.Instant

/** Генерирует GPX 1.1 из GPS-трека тренировки для экспорта/шаринга. */
object GpxExporter {

    fun buildGpx(sportType: SportType, points: List<TrackPoint>): String {
        val name = trackName(sportType, points.firstOrNull()?.timestampMs)
        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<gpx version="1.1" creator="Competra" xmlns="http://www.topografix.com/GPX/1/1">""")
            appendLine("  <metadata>")
            appendLine("    <name>${escape(name)}</name>")
            appendLine("  </metadata>")
            appendLine("  <trk>")
            appendLine("    <name>${escape(name)}</name>")
            appendLine("    <trkseg>")
            points.forEach { point ->
                appendLine("      <trkpt lat=\"${point.lat}\" lon=\"${point.lon}\">")
                appendLine("        <time>${Instant.ofEpochMilli(point.timestampMs)}</time>")
                appendLine("      </trkpt>")
            }
            appendLine("    </trkseg>")
            appendLine("  </trk>")
            appendLine("</gpx>")
        }
    }

    fun fileName(sportType: SportType, startedAtMs: Long?): String {
        val date = startedAtMs?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
        val safeDate = date.toString().substringBefore("T").replace("-", "")
        return "${sportType.name.lowercase()}_$safeDate.gpx"
    }

    private fun trackName(sportType: SportType, startedAtMs: Long?): String {
        val label = when (sportType) {
            SportType.RUNNING -> "Бег"
            SportType.CYCLING -> "Велоспорт"
            SportType.SKIING -> "Лыжи"
        }
        val date = (startedAtMs?.let { Instant.ofEpochMilli(it) } ?: Instant.now()).toString().substringBefore("T")
        return "$label $date"
    }

    private fun escape(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
