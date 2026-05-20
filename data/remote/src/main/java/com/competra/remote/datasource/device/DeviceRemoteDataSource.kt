package com.competra.remote.datasource.device

import com.competra.remote.base.CommonModel
import com.competra.remote.request.device.FcmTokenRequest
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface DeviceRemoteDataSource {

    @POST("devices/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Result<CommonModel<Any>>

    @HTTP(method = "DELETE", path = "devices/fcm-token", hasBody = true)
    suspend fun unregisterFcmToken(@Body request: FcmTokenRequest): Result<CommonModel<Any>>
}
