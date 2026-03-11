package com.rodionov.center.presentation.draw

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.draw.DrawAction
import com.rodionov.center.presentation.participant_list.ParticipantList
import com.rodionov.resources.R
import org.koin.androidx.compose.koinViewModel

/**
 * Экран для жеребьевки участников соревнования
 * */
@Composable
fun DrawParticipantsScreen(viewModel: DrawViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val userAction = remember { viewModel::onAction }
    Box(modifier = Modifier.fillMaxSize()) {
        ParticipantList(state.participants) {}
        FloatingActionButton(
            onClick = {
                userAction.invoke(DrawAction.StartDrawOperation)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = Dimens.SIZE_BASE.dp, end = Dimens.SIZE_BASE.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.play_arrow_24px),
                contentDescription = null,
            )
        }
    }
}