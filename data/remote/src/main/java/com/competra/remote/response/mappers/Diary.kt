package com.competra.remote.response.mappers

import com.competra.domain.models.diary.BikeDetails
import com.competra.domain.models.diary.RunDetails
import com.competra.domain.models.diary.SkiDetails
import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.Workout
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.remote.response.diary.WorkoutResponse

/**
 * Преобразует ответ сервера в доменную модель. Локальный [Workout.id] здесь не заполняется —
 * его сохраняет вызывающая сторона (см. [WorkoutSyncOrchestrator] zip с исходной локальной записью).
 */
fun WorkoutResponse.toDomain(): WorkoutWithDetails = WorkoutWithDetails(
    workout = Workout(
        remoteId = id,
        sportType = SportType.valueOf(sportType),
        status = WorkoutStatus.valueOf(status),
        scheduledDate = scheduledDate,
        startedAt = startedAt,
        durationSeconds = durationSeconds,
        distanceMeters = distanceMeters,
        elevationGainMeters = elevationGainMeters,
        notes = notes,
        isSynced = true,
        serverUpdatedAt = updatedAt.takeIf { it > 0L }
    ),
    runDetails = runDetails?.let { RunDetails(workoutId = 0, cadenceSpm = it.cadenceSpm) },
    bikeDetails = bikeDetails?.let { BikeDetails(workoutId = 0, cadenceRpm = it.cadenceRpm, powerWatts = it.powerWatts) },
    skiDetails = skiDetails?.let { SkiDetails(workoutId = 0, style = SkiStyle.valueOf(it.style)) }
)
