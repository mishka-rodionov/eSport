package com.competra.center.presentation.score_graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.ui.components.ScoreGraphChart
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScoreGraphScreen(
    groupId: Long,
    competitionId: String,
    viewModel: ScoreGraphViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(groupId, competitionId) {
        viewModel.load(groupId, competitionId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.data == null || state.data!!.series.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Нет финишировавших участников для построения графика",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            else -> Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = state.groupTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(Dimens.SIZE_BASE.dp),
                )
                ScoreGraphChart(
                    data = state.data!!,
                    visibleParticipantIds = state.visibleParticipantIds,
                    highlightedParticipantId = state.highlightedParticipantId,
                    onToggleVisibility = viewModel::toggleParticipantVisibility,
                    onToggleHighlight = viewModel::toggleHighlight,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
