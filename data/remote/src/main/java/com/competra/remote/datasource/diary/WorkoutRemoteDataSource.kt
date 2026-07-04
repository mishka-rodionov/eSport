package com.competra.remote.datasource.diary

import com.competra.remote.base.CommonModel
import com.competra.remote.request.diary.WorkoutRequest
import com.competra.remote.response.diary.WorkoutResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkoutRemoteDataSource {

    @POST("diary/workouts")
    suspend fun saveWorkouts(@Body requests: List<WorkoutRequest>): Result<CommonModel<List<WorkoutResponse>>>

    @GET("diary/workouts")
    suspend fun getWorkouts(): Result<CommonModel<List<WorkoutResponse>>>

    @DELETE("diary/workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: Long): Result<CommonModel<Any>>
}
