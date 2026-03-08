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

/**
 * Экран для отображения данных, считанных с чипа участника соревнований по ориентированию.
 *
 * Отображает информацию об участнике (группа, фамилия, имя, время старта) и его результаты
 * (время финиша, общее время и сплиты по контрольным пунктам).
 *
 * @param viewModel ViewModel для управления состоянием экрана.
 */
@Composable
fun OrientReadCardScreen(viewModel: OrientReadCardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    state.participant?.let { participant ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { ReadScreenItem("Группа: ${participant.groupName}") }
            item { ReadScreenItem("Фамилия: ${participant.lastName}") }
            item { ReadScreenItem("Имя: ${participant.firstName}") }
            item { ReadScreenItem("Старт: ${participant.startTime}") }

            state.participantResult?.let { result ->
                item { ReadScreenItem("Финиш: ${result.finishTime}") }
                item { ReadScreenItem("Результат: ${result.totalTime?.toRaceTime()}") }

                result.splits?.let { splitTimes ->
                    itemsIndexed(splitTimes) { index, item ->
                        val time = if (index == 0) {
                            (item.timestamp - participant.startTime).toSplitTime()
                        } else {
                            (item.timestamp - splitTimes[index - 1].timestamp).toSplitTime()
                        }
                        ReadScreenItem("КП №${item.controlPoint}: $time")
                    }
                }
            }
        }
    }
}

/**
 * Элемент списка для отображения текстовой информации на экране чтения карты.
 *
 * @param text Текст для отображения.
 */
@Composable
fun ReadScreenItem(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SIZE_BASE_HALF.dp),
        text = text
    )
}