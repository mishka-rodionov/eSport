package com.rodionov.domain.repository

interface UploadRepository {
    /**
     * Загружает файл на сервер и возвращает публичный URL.
     *
     * @param fileBytes Байты файла.
     * @param fileName Имя файла с расширением.
     * @param type Тип загружаемого файла: avatar | competition_image | competition_map.
     */
    suspend fun uploadFile(fileBytes: ByteArray, fileName: String, type: String): Result<String>
}
