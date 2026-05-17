package com.competra.remote.repository

import com.competra.domain.repository.UploadRepository
import com.competra.remote.datasource.upload.UploadRemoteDataSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadRepositoryImpl(
    private val dataSource: UploadRemoteDataSource
) : UploadRepository {

    override suspend fun uploadFile(fileBytes: ByteArray, fileName: String, type: String): Result<String> {
        val requestBody = fileBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
        val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
        return dataSource.uploadFile(part, typePart)
            .mapCatching { it.result?.url ?: error("No URL in upload response") }
    }
}
