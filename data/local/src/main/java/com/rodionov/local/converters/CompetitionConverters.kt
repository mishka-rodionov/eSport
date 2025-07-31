package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CompetitionConverters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    // LocalDate
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.format(formatter)

    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? = date?.let { LocalDate.parse(it, formatter) }

    // KindOfSport
    @TypeConverter
    fun fromKindOfSport(kind: KindOfSport?): String? = kind?.name

    @TypeConverter
    fun toKindOfSport(name: String?): KindOfSport? =
        KindOfSport.all.find { it.name == name }

    // Coordinates (serialize как строку "lat,lon")
    @TypeConverter
    fun fromCoordinates(coord: Coordinates?): String? =
        coord?.let { "${it.latitude},${it.longitude}" }

    @TypeConverter
    fun toCoordinates(value: String?): Coordinates? =
        value?.split(",")?.takeIf { it.size == 2 }?.let {
            Coordinates(it[0].toDouble(), it[1].toDouble())
        }
}