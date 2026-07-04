package com.competra.ui.components

import com.competra.designsystem.components.FractionalRect
import com.competra.domain.models.CropRect

/**
 * Мапперы между доменной моделью [CropRect] и презентационным [FractionalRect] из
 * `:core:designsystem`. Живут здесь, а не в самом designsystem, т.к. этот модуль
 * не должен зависеть от `:domain` — только `:core:ui` уже зависит и от того, и от
 * другого одновременно.
 */
fun CropRect.toFractionalRect(): FractionalRect = FractionalRect(
    x = x.toFloat(),
    y = y.toFloat(),
    width = width.toFloat(),
    height = height.toFloat()
)

fun FractionalRect.toCropRect(): CropRect = CropRect(
    x = x.toDouble(),
    y = y.toDouble(),
    width = width.toDouble(),
    height = height.toDouble()
)
