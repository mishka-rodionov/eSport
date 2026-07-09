package com.competra.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream

/**
 * Сжимает изображения перед загрузкой на сервер: уменьшает разрешение и
 * пережимает в JPEG с заданным качеством. Декодирование идёт в два прохода
 * (bounds → sampled bitmap), чтобы не грузить оригинал целиком в память —
 * важно для фото с камеры, которые могут весить десятки мегапикселей.
 */
object ImageCompressor {

    private const val ROTATE_90_DEGREES = 90
    private const val ROTATE_180_DEGREES = 180
    private const val ROTATE_270_DEGREES = 270

    /**
     * Готовые пресеты сжатия под конкретные места использования в приложении.
     */
    enum class Preset(val maxWidthPx: Int, val quality: Int) {
        AVATAR(maxWidthPx = 1080, quality = 90),
        COMPETITION_COVER(maxWidthPx = 2048, quality = 85),
    }

    /**
     * Декодирует изображение по [uri], уменьшает его до [maxWidthPx] по ширине
     * (с сохранением пропорций), поворачивает согласно EXIF-ориентации и
     * сжимает в JPEG с заданным [quality] (0-100).
     *
     * @return байты JPEG или null, если изображение не удалось декодировать.
     */
    fun compress(context: Context, uri: Uri, maxWidthPx: Int, quality: Int): ByteArray? {
        val sampledBitmap = decodeSampledBitmap(context, uri, maxWidthPx) ?: return null

        val rotationDegrees = context.contentResolver.openInputStream(uri)?.use {
            readExifRotationDegrees(it)
        } ?: 0

        val rotatedBitmap = if (rotationDegrees != 0) {
            rotateBitmap(sampledBitmap, rotationDegrees)
        } else {
            sampledBitmap
        }

        val resizedBitmap = if (rotatedBitmap.width > maxWidthPx) {
            val targetHeight = (rotatedBitmap.height * maxWidthPx / rotatedBitmap.width.toFloat()).toInt()
            Bitmap.createScaledBitmap(rotatedBitmap, maxWidthPx, targetHeight, true)
        } else {
            rotatedBitmap
        }

        return ByteArrayOutputStream().use { output ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
            output.toByteArray()
        }
    }

    private fun decodeSampledBitmap(context: Context, uri: Uri, maxWidthPx: Int): Bitmap? {
        val resolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val boundsStream = resolver.openInputStream(uri) ?: return null
        boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }

        val sampledOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxWidthPx)
        }
        return resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, sampledOptions)
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxWidthPx: Int): Int {
        var inSampleSize = 1
        var sampledWidth = width
        var sampledHeight = height
        while (sampledWidth / 2 >= maxWidthPx) {
            sampledWidth /= 2
            sampledHeight /= 2
            inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun readExifRotationDegrees(inputStream: java.io.InputStream): Int {
        val orientation = ExifInterface(inputStream)
            .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> ROTATE_90_DEGREES
            ExifInterface.ORIENTATION_ROTATE_180 -> ROTATE_180_DEGREES
            ExifInterface.ORIENTATION_ROTATE_270 -> ROTATE_270_DEGREES
            else -> 0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
