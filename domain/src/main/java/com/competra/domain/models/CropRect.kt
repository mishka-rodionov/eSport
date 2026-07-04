package com.competra.domain.models

/**
 * Область кропа изображения в нормализованных координатах (0..1) относительно
 * оригинального, неизменённого файла, который хранится на сервере.
 *
 * @property x Левая граница рамки (доля ширины изображения).
 * @property y Верхняя граница рамки (доля высоты изображения).
 * @property width Ширина рамки (доля ширины изображения).
 * @property height Высота рамки (доля высоты изображения).
 */
data class CropRect(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)
