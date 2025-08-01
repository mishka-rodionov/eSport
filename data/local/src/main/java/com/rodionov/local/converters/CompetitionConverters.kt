package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.OrienteeringDirection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//class CompetitionConverters {
//
//    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
//
//    // LocalDate
//    @TypeConverter
//    fun fromLocalDate(date: LocalDate?): String? = date?.format(formatter)
//
//    @TypeConverter
//    fun toLocalDate(date: String?): LocalDate? = date?.let { LocalDate.parse(it, formatter) }
//
//    // KindOfSport
//    @TypeConverter
//    fun fromKindOfSport(kind: KindOfSport?): String? = kind?.name
//
//    @TypeConverter
//    fun toKindOfSport(name: String?): KindOfSport? =
//        KindOfSport.all.find { it.name == name }
//
//    // Coordinates (serialize как строку "lat,lon")
//    @TypeConverter
//    fun fromCoordinates(coord: Coordinates?): String? =
//        coord?.let { "${it.latitude},${it.longitude}" }
//
//    @TypeConverter
//    fun toCoordinates(value: String?): Coordinates? =
//        value?.split(",")?.takeIf { it.size == 2 }?.let {
//            Coordinates(it[0].toDouble(), it[1].toDouble())
//        }
//}

// Converters.kt
class CompetitionConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, dateFormatter)
        }
    }

    // Если вы решите хранить OrienteeringDirection как String (рекомендуется)
    @TypeConverter
    fun fromOrienteeringDirection(direction: OrienteeringDirection?): String? {
        return direction?.name
    }

    @TypeConverter
    fun toOrienteeringDirection(value: String?): OrienteeringDirection? {
        return value?.let { OrienteeringDirection.valueOf(it) }
    }

    // Если вы решите хранить KindOfSport по имени (рекомендуется)
    @TypeConverter
    fun fromKindOfSport(kindOfSport: KindOfSport?): String? {
        return kindOfSport?.name
    }

    @TypeConverter
    fun toKindOfSport(name: String?): KindOfSport? {
        return name?.let { sportName ->
            KindOfSport.all.find { it.name == sportName }
        }
    }
}