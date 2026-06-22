package com.competra.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

// База — стандартная Material 3 type scale (размеры/межстрочные оставляем дефолтные),
// переопределяем только акцентные веса (см. core/designsystem/DESIGN.md).
private val default = Typography()

val Typography = default.copy(
    headlineSmall = default.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = default.labelLarge.copy(fontWeight = FontWeight.Medium),
)
