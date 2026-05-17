package com.competra.remote.datasource.upload

import com.competra.remote.base.CommonModel
import com.competra.remote.response.upload.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadRemoteDataSource {

    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): Result<CommonModel<UploadResponse>>
}
