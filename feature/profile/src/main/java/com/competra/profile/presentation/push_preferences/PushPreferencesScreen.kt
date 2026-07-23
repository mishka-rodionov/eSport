package com.competra.profile.presentation.push_preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.competra.designsystem.colors.LightColors
import com.competra.designsystem.theme.Dimens
import org.koin.androidx.compose.koinViewModel

/**
 * Экран настроек push-уведомлений: независимые тумблеры по категориям.
 */
@Composable
fun PushPreferencesScreen(viewModel: PushPreferencesViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PushPreferencesContent(state = state, onAction = viewModel::onAction)
        }
    }
}

@Composable
private fun PushPreferencesContent(state: PushPreferencesState, onAction: (PushPreferencesAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(Dimens.SIZE_BASE.dp)) {
        Text(
            text = "Push-уведомления",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                PushPreferenceRow(
                    title = "Результаты опубликованы",
                    checked = state.resultsPublished,
                    onCheckedChange = { onAction(PushPreferencesAction.ToggleResultsPublished(it)) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimens.SIZE_BASE.dp),
                    thickness = 0.5.dp,
                    color = LightColors.greyB8.copy(alpha = 0.3f)
                )
                PushPreferenceRow(
                    title = "Старт соревнования",
                    checked = state.competitionStart,
                    onCheckedChange = { onAction(PushPreferencesAction.ToggleCompetitionStart(it)) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = Dimens.SIZE_BASE.dp),
                    thickness = 0.5.dp,
                    color = LightColors.greyB8.copy(alpha = 0.3f)
                )
                PushPreferenceRow(
                    title = "За сутки до старта",
                    checked = state.dayBeforeReminder,
                    onCheckedChange = { onAction(PushPreferencesAction.ToggleDayBeforeReminder(it)) }
                )
            }
        }
    }
}

@Composable
private fun PushPreferenceRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SIZE_BASE.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
