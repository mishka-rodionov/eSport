package com.competra.designsystem.components

/**
 * Прямоугольная область в нормализованных координатах (0..1) относительно какого-то
 * внешнего изображения. Чисто презентационный тип: [core:designsystem][com.competra.designsystem]
 * не должен зависеть от доменной модели, поэтому смысл (например, "область кропа") и
 * маппинг в/из доменных типов остаются на стороне вызывающего кода.
 *
 * @property x Левая граница (доля ширины).
 * @property y Верхняя граница (доля высоты).
 * @property width Ширина (доля ширины).
 * @property height Высота (доля высоты).
 */
data class FractionalRect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
