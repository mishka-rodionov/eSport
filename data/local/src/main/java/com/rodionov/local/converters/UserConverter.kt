package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.Qualification
import com.rodionov.domain.models.SportsCategory

class UserConverter {

    @TypeConverter
    fun fromGender(gender: Gender?): String? {
        return gender?.name
    }

    @TypeConverter
    fun toGender(gender: String?): Gender? {
        return Gender.entries.firstOrNull { it.name == gender }
    }

    @TypeConverter
    fun fromQualifications(qualifications: List<Qualification>?): String? {
        return qualifications?.joinToString("_") { "(${it.kindOfSport.name}, ${it.sportsCategory.name})" }
    }

    @TypeConverter
    fun toQualifications(qualifications: String?): List<Qualification>? {
        return qualifications?.split("_")?.map { it.toQualification() }
    }

}

fun String.toQualification(): Qualification {
    val (kindOfSport, sportsCategory) = this.split(",")

    return Qualification(KindOfSport.all.first { it.name == kindOfSport }, SportsCategory.entries.first { it.name == sportsCategory })
}