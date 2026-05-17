package com.competra.local.converters

import androidx.room.TypeConverter
import com.competra.domain.models.Gender
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.Qualification
import com.competra.domain.models.SportsCategory

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
        return if (qualifications?.isNotEmpty() == true) qualifications.split("_").map { it.toQualification() } else emptyList()
    }

}

fun String.toQualification(): Qualification {
    val (kindOfSport, sportsCategory) = this.split(",")

    return Qualification(KindOfSport.all.first { it.name == kindOfSport }, SportsCategory.entries.first { it.name == sportsCategory })
}