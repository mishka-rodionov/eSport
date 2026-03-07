package com.rodionov.center.presentation.read_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.theme.Dimens
import com.rodionov.utils.orienteering.toRaceTime
import com.rodionov.utils.orienteering.toSplitTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrientReadCardScreen(viewModel: OrientReadCardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    state.participant?.let { participant ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ReadScreenItem("Группа: ${participant.groupName}")
            ReadScreenItem("Фамилия: ${participant.lastName}")
            ReadScreenItem("Имя: ${participant.firstName}")
            ReadScreenItem("Старт: ${participant.startTime}")
            state.participantResult?.let { result ->
                ReadScreenItem("Финиш: ${result.finishTime}")
                ReadScreenItem("Результат: ${result.totalTime?.toRaceTime()}")
                result.splits?.let { splitTimes ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(splitTimes) { index, item ->
                            if (index == 0) {
                                ReadScreenItem("КП №${item.controlPoint}: ${(item.timestamp - participant.startTime).toSplitTime()}")
                            } else {
                                ReadScreenItem("КП №${item.controlPoint}: ${(item.timestamp - splitTimes[index - 1].timestamp).toSplitTime()}")
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ReadScreenItem(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SIZE_BASE_HALF.dp),
        text = text
    )
}