package com.competra.remote.request.mappers

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.remote.request.diary.BikeDetailsRequest
import com.competra.remote.request.diary.RunDetailsRequest
import com.competra.remote.request.diary.SkiDetailsRequest
import com.competra.remote.request.diary.WorkoutRequest

fun WorkoutWithDetails.toRequest(): WorkoutRequest = WorkoutRequest(
    workoutId = workout.remoteId,
    sportType = workout.sportType.name,
    status = workout.status.name,
    scheduledDate = workout.scheduledDate,
    startedAt = workout.startedAt,
    durationSeconds = workout.durationSeconds,
    distanceMeters = workout.distanceMeters,
    elevationGainMeters = workout.elevationGainMeters,
    notes = workout.notes,
    runDetails = runDetails?.let { RunDetailsRequest(cadenceSpm = it.cadenceSpm) },
    bikeDetails = bikeDetails?.let { BikeDetailsRequest(cadenceRpm = it.cadenceRpm, powerWatts = it.powerWatts) },
    skiDetails = skiDetails?.let { SkiDetailsRequest(style = it.style.name) }
)
