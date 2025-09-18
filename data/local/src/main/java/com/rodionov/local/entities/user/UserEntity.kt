package com.rodionov.local.entities.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.Qualification
import com.rodionov.local.converters.UserConverter

@Entity(tableName = "users")
@TypeConverters(UserConverter::class)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?, // отчество
    val birthDate: String,
    val gender: Gender,
    val photo: String,
    val phoneNumber: String?,
    val email: String,
    val qualification: List<Qualification>
)