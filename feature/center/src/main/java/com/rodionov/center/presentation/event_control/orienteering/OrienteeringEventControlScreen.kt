package com.rodionov.center.presentation.event_control.orienteering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.center.data.event_control.OrienteeringEventControlState
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.resources.R
import org.koin.androidx.compose.koinViewModel

/**
 * Главный экран управления событием по ориентированию.
 */
@Composable
fun OrienteeringEventControlScreen(
    viewModel: OrienteeringEventControlViewModel = koinViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val state by viewModel.state.collectAsState()
    val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(Dimens.SIZE_BASE.dp)
        ) {
            Text(
                text = state.competitionTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Панель управления чипами (Сеткой)
            SectionHeader(title = "Панель управления")
            OrienteeringEventControlContent(isExpanded, viewModel::onAction)

            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

            // Статус и таймер
            if (state.isTimerRunning || state.competition?.startTime != null) {
                TimerSection(state = state)
                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
            }

            // Основные действия
            SectionHeader(title = "Действия")
            
            ControlActionButton(
                text = "Выдать чипы",
                icon = R.drawable.ic_add_24px,
                onClick = { viewModel.onAction(OrientEventControlAction.OpenGetOrienteeringChip) }
            )

            if ((state.competition?.startTimeMode == StartTimeMode.USER_SET || state.competition?.startTimeMode == StartTimeMode.STRICT) && !state.isTimerRunning && state.competition?.startTime == null) {
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = { viewModel.onAction(OrientEventControlAction.StartCompetition) },
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ЗАПУСТИТЬ ТАЙМЕР", fontWeight = FontWeight.Bold)
                }
            }

            if (state.isCompetitionRunning) {
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = { viewModel.onAction(OrientEventControlAction.StopCompetition) },
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ЗАВЕРШИТЬ СОРЕВНОВАНИЕ", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Навигация по разделам
            SectionHeader(title = "Разделы")
            
            NavigationRow(
                text = "Список участников",
                onClick = { viewModel.onAction(OrientEventControlAction.OpenParticipantLists) }
            )
            NavigationRow(
                text = "Жеребьёвка",
                onClick = { viewModel.onAction(OrientEventControlAction.OpenDrawParticipants) }
            )
            NavigationRow(
                text = "Результаты",
                onClick = { viewModel.onAction(OrientEventControlAction.OpenResults) }
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = Dimens.SIZE_HALF.dp)
    )
}

@Composable
private fun TimerSection(state: OrienteeringEventControlState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SIZE_BASE.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val label = if (state.isTimerRunning) "До старта" else "Соревнование начато"
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            
            if (state.isTimerRunning) {
                val minutes = (state.countdownMillis / 1000) / 60
                val seconds = (state.countdownMillis / 1000) % 60
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "СТАРТ ДАН",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ControlActionButton(text: String, icon: Int, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun NavigationRow(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickRipple(onClick = onClick),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.SIZE_BASE.dp, vertical = Dimens.SIZE_BASER.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrienteeringEventControlContent(
    isExpanded: Boolean,
    userAction: (OrientEventControlAction) -> Unit
) {
    if (isExpanded) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ControlGridItem(Modifier.weight(1f), "Сканировать", Color(0xFF2196F3), R.drawable.edit) {
                userAction(OrientEventControlAction.OpenOrientReadCard)
            }
            ControlGridItem(Modifier.weight(1f), "Очистить", Color(0xFF4CAF50), R.drawable.edit) {}
            ControlGridItem(Modifier.weight(1f), "Проверить", Color(0xFFFFC107), R.drawable.edit) {}
            ControlGridItem(Modifier.weight(1f), "Записать", Color(0xFFF44336), R.drawable.edit) {}
        }
    } else {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                ControlGridItem(Modifier.weight(1f), "Сканировать", Color(0xFF2196F3), R.drawable.edit) {
                    userAction(OrientEventControlAction.OpenOrientReadCard)
                }
                ControlGridItem(Modifier.weight(1f), "Очистить", Color(0xFF4CAF50), R.drawable.edit) {}
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                ControlGridItem(Modifier.weight(1f), "Проверить", Color(0xFFFFC107), R.drawable.edit) {}
                ControlGridItem(Modifier.weight(1f), "Записать", Color(0xFFF44336), R.drawable.edit) {}
            }
        }
    }
}

@Composable
private fun ControlGridItem(
    modifier: Modifier,
    text: String,
    color: Color,
    icon: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1.1f)
            .clickRipple(onClick = onClick),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Compact", showBackground = true)
@Composable
fun OrienteeringEventControlScreenPreviewCompact() {
    MaterialTheme {
        OrienteeringEventControlContent(isExpanded = false, userAction = {})
    }
}
