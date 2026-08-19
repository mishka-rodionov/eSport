package com.competra.ui.components

import com.patrykandpatrick.vico.compose.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.compose.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

/**
 * [Decoration], отмечающая вертикальной линией фиксированное значение X. Vico из коробки даёт
 * только [com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine] (для отметки
 * значения Y) — готового аналога для X нет, поэтому здесь тот же принцип отрисовки, что и у
 * штатного HorizontalLine, но по оси X. Используется для отметки контрольного времени группы на
 * графике очков BY_CHOICE.
 */
internal class VerticalLine(
    private val x: (ExtraStore) -> Double,
    private val line: LineComponent,
) : Decoration {
    override fun drawOverLayers(context: CartesianDrawingContext) {
        with(context) {
            if (ranges.xLength <= 0.0) return
            val xValue = x(model.extraStore)
            if (xValue < ranges.minX || xValue > ranges.maxX) return
            val canvasX = layerBounds.left + ((xValue - ranges.minX) / ranges.xLength).toFloat() * layerBounds.width
            line.drawVertical(context, canvasX, layerBounds.top, layerBounds.bottom)
        }
    }
}
