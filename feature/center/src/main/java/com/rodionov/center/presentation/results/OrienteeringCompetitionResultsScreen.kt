package com.rodionov.center.presentation.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.colors.LightColors
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantWithResult

@Composable
fun OrienteeringCompetitionResultsScreen() {

}

@Composable
fun ParticipantItem(result: ParticipantWithResult) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_TWO.dp)
        ) {
            Text(
                text = result.result?.rank ?: result.result?.status,
                modifier = Modifier.weight(0.1F)
            )
            Text(
                text = "${participant.firstName} ${participant.lastName}",
                modifier = Modifier.weight(0.9F)
            )
            Text(
                text = participant.startTime.toString(),
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