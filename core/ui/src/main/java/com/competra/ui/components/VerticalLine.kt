package com.competra.ui.components

import com.patrykandpatrick.vico.compose.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.compose.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

/**
 * [Decoration], отмечающая вертикальной линией фиксированное значение X. Vico из коробки даёт
 * только [com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine] (для отметки
 * значения Y) — готового аналога для X нет.
 *
 * Позиционирование скопировано 1:1 с того, как сам [com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer]
 * переводит X-значения точек в пиксели (см. `drawInternal`/`collectPointsAndVisibleIndexRange` в
 * исходниках vico-compose): точки располагаются не линейным растяжением диапазона [minX, maxX] на
 * всю ширину layerBounds, а через шаг `xSpacing` на единицу `xStep`, от `drawingStart`
 * (с поправкой на паддинг и текущий скролл графика). Первая версия этого класса использовала
 * упрощённую линейную формулу по ширине — она расходилась с реальными координатами кривых.
 */
internal class VerticalLine(
    private val x: (ExtraStore) -> Double,
    private val line: LineComponent,
) : Decoration {
    override fun drawOverLayers(context: CartesianDrawingContext) {
        with(context) {
            if (ranges.xStep <= 0.0) return
            val xValue = x(model.extraStore)
            val boundsStart = if (isLtr) layerBounds.left else layerBounds.right
            val drawingStartAlignmentCorrection = layoutDirectionMultiplier * layerDimensions.startPadding
            val drawingStart = boundsStart + drawingStartAlignmentCorrection - scroll
            val canvasX = drawingStart +
                layoutDirectionMultiplier * layerDimensions.xSpacing * ((xValue - ranges.minX) / ranges.xStep).toFloat()
            if (canvasX < layerBounds.left || canvasX > layerBounds.right) return
            line.drawVertical(context, canvasX, layerBounds.top, layerBounds.bottom)
        }
    }
}
