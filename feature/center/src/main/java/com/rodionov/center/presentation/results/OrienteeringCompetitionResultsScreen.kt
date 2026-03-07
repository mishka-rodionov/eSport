package com.rodionov.center.presentation.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.colors.LightColors
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrienteeringCompetitionResultsScreen(
    viewModel: OrienteeringCompetitionResultsViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        state.groupsWithParticipantsAndResults.firstOrNull()?.participants?.let{
            items(it) { result ->
                ParticipantItem(result)
            }
        }
    }

}

@Composable
fun ParticipantItem(result: ParticipantWithResult) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_TWO.dp)
        ) {
            Text(
                text = result.result?.rank?.toString() ?: result.result?.status?.name ?: "UNK",
                modifier = Modifier.weight(0.1F)
            )
            Text(
                text = "${result.participant.firstName} ${result.participant.lastName}",
                modifier = Modifier.weight(0.9F)
            )
            Text(
                text = result.participant.startTime.toString(),
                modifier = Modifier.weight(0.2F)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp),
            thickness = Dimens.SIZE_SINGLE.dp,
            color = LightColors.greyB8
        )
    }
}