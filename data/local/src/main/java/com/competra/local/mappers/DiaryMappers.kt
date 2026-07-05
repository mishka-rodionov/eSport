package com.competra.local.mappers

import com.competra.domain.models.diary.BikeDetails
import com.competra.domain.models.diary.RunDetails
import com.competra.domain.models.diary.SkiDetails
import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.Workout
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.local.entities.diary.BikeDetailsEntity
import com.competra.local.entities.diary.RunDetailsEntity
import com.competra.local.entities.diary.SkiDetailsEntity
import com.competra.local.entities.diary.WorkoutEntity
import com.competra.local.entities.diary.WorkoutWithDetailsEntity

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    remoteId = remoteId,
    sportType = sportType.name,
    status = status.name,
    scheduledDate = scheduledDate,
    startedAt = startedAt,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    elevationGainMeters = elevationGainMeters,
    notes = notes,
    trackEncoded = trackEncoded,
    isSynced = isSynced,
    lastModified = lastModified,
    isDeleted = isDeleted,
    serverUpdatedAt = serverUpdatedAt,
    syncError = syncError
)

fun WorkoutEntity.toDomain(): Workout = Workout(
    id = id,
    remoteId = remoteId,
    sportType = SportType.valueOf(sportType),
    status = WorkoutStatus.valueOf(status),
    scheduledDate = scheduledDate,
    startedAt = startedAt,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    elevationGainMeters = elevationGainMeters,
    notes = notes,
    trackEncoded = trackEncoded,
    isSynced = isSynced,
    lastModified = lastModified,
    isDeleted = isDeleted,
    serverUpdatedAt = serverUpdatedAt,
    syncError = syncError
)

fun RunDetails.toEntity(): RunDetailsEntity = RunDetailsEntity(workoutId = workoutId, cadenceSpm = cadenceSpm)
fun RunDetailsEntity.toDomain(): RunDetails = RunDetails(workoutId = workoutId, cadenceSpm = cadenceSpm)

fun BikeDetails.toEntity(): BikeDetailsEntity =
    BikeDetailsEntity(workoutId = workoutId, cadenceRpm = cadenceRpm, powerWatts = powerWatts)
fun BikeDetailsEntity.toDomain(): BikeDetails =
    BikeDetails(workoutId = workoutId, cadenceRpm = cadenceRpm, powerWatts = powerWatts)

fun SkiDetails.toEntity(): SkiDetailsEntity = SkiDetailsEntity(workoutId = workoutId, style = style.name)
fun SkiDetailsEntity.toDomain(): SkiDetails = SkiDetails(workoutId = workoutId, style = SkiStyle.valueOf(style))

fun WorkoutWithDetailsEntity.toDomain(): WorkoutWithDetails = WorkoutWithDetails(
    workout = workout.toDomain(),
    runDetails = runDetails?.toDomain(),
    bikeDetails = bikeDetails?.toDomain(),
    skiDetails = skiDetails?.toDomain()
)
