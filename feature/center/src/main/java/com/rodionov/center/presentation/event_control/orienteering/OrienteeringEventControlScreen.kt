package com.rodionov.center.presentation.event_control.orienteering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.event_control.OrientEventControlAction
import org.koin.androidx.compose.koinViewModel

private const val WEIGHT_DEFAULT = 1f
private const val ASPECT_RATIO_DEFAULT = 1f
private const val PADDING_DEFAULT = 4
private const val CORNER_SHAPE_DEFAULT = 120

/**
 * Composable-функция, которая служит главным экраном для управления событием по спортивному ориентированию.
 * Она адаптирует свой макет в зависимости от доступной ширины экрана, определяемой [windowSizeClass].
 * Для широких экранов кнопки управления отображаются в один ряд, а для узких — в виде сетки 2x2.
 * Этот экран обрабатывает взаимодействия с пользователем, делегируя действия предоставленной [viewModel].
 *
 * @param viewModel Модель представления [OrienteeringEventControlViewModel], отвечающая за бизнес-логику и состояние экрана. Получается через [koinViewModel] из Koin.
 * @param windowSizeClass [WindowSizeClass] текущего окна, используется для определения, должен ли макет быть расширенным или компактным.
 */
@Composable
fun OrienteeringEventControlScreen(
    viewModel: OrienteeringEventControlViewModel = koinViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val state by viewModel.state.collectAsState()
    val isExpanded =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = state.competitionTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OrienteeringEventControlContent(isExpanded, viewModel::onAction)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {
                viewModel.onAction(OrientEventControlAction.OpenGetOrienteeringChip)
            },
            content = {
                Text("Выдать чипы")
            }
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = Dimens.SIZE_BASE.dp),
            onClick = {
                viewModel.onAction(OrientEventControlAction.OpenParticipantLists)
            },
            content = {
                Text("Список участников")
            }
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = Dimens.SIZE_BASE.dp),
            onClick = {
                viewModel.onAction(OrientEventControlAction.OpenDrawParticipants)
            },
            content = {
                Text("Жеребьёвка участников")
            }
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = Dimens.SIZE_BASE.dp),
            onClick = {
                viewModel.onAction(OrientEventControlAction.OpenResults)
            },
            content = {
                Text("Результаты соревнований")
            }
        )
    }
}

@Composable
fun OrienteeringEventControlContent(
    isExpanded: Boolean,
    userAction: (OrientEventControlAction) -> Unit
) {
    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(WEIGHT_DEFAULT)
                    .aspectRatio(ASPECT_RATIO_DEFAULT)
                    .padding(PADDING_DEFAULT.dp)
                    .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                    .background(Color.Blue), onClick = {
                    userAction.invoke(OrientEventControlAction.OpenOrientReadCard)
                }) {
                Text(text = "Сканировать")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(WEIGHT_DEFAULT)
                    .aspectRatio(ASPECT_RATIO_DEFAULT)
                    .padding(PADDING_DEFAULT.dp)
                    .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                    .background(Color.Green), onClick = {}) {
                Text(text = "Очистить")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(WEIGHT_DEFAULT)
                    .aspectRatio(ASPECT_RATIO_DEFAULT)
                    .padding(PADDING_DEFAULT.dp)
                    .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                    .background(Color.Yellow), onClick = {}) {
                Text(text = "Проверить")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(WEIGHT_DEFAULT)
                    .aspectRatio(ASPECT_RATIO_DEFAULT)
                    .padding(PADDING_DEFAULT.dp)
                    .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                    .background(Color.Red), onClick = {}) {
                Text(text = "Записать")
            }
        }
    } else {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(WEIGHT_DEFAULT)
                        .aspectRatio(ASPECT_RATIO_DEFAULT)
                        .padding(PADDING_DEFAULT.dp)
                        .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                        .background(Color.Blue), onClick = {
                        userAction.invoke(OrientEventControlAction.OpenOrientReadCard)
                    }) {
                    Text(text = "Сканировать")
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(WEIGHT_DEFAULT)
                        .aspectRatio(ASPECT_RATIO_DEFAULT)
                        .padding(PADDING_DEFAULT.dp)
                        .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                        .background(Color.Green), onClick = {}) {
                    Text(text = "Очистить")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(WEIGHT_DEFAULT)
                        .aspectRatio(ASPECT_RATIO_DEFAULT)
                        .padding(PADDING_DEFAULT.dp)
                        .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                        .background(Color.Yellow), onClick = {}) {
                    Text(text = "Проверить")
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(WEIGHT_DEFAULT)
                        .aspectRatio(ASPECT_RATIO_DEFAULT)
                        .padding(PADDING_DEFAULT.dp)
                        .clip(RoundedCornerShape(CORNER_SHAPE_DEFAULT.dp))
                        .background(Color.Red), onClick = {}) {
                    Text(text = "Записать")
                }
            }
        }
    }

}

@Preview(name = "Compact", showBackground = true)
@Composable
fun OrienteeringEventControlScreenPreviewCompact() {
    OrienteeringEventControlContent(isExpanded = false, userAction = {})
}

@Preview(name = "Expanded", showBackground = true, widthDp = 840)
@Composable
fun OrienteeringEventControlScreenPreviewExpanded() {
    OrienteeringEventControlContent(isExpanded = true, userAction = {})
}
