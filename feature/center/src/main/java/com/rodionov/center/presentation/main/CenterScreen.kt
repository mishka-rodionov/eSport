package com.rodionov.center.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.main.CenterState
import com.rodionov.domain.models.Competition
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun CenterScreen(viewModel: CenterViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsState()

    Column {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {
                viewModel.handleEffects(CenterEffects.OpenKindOfSports)
            },
            content = {
                Text("Создать новое событие")
            }
        )
        ControlledEvents(state, viewModel::handleEffects)
    }
}

@Composable
fun ControlledEvents(state: CenterState, userAction: (CenterEffects) -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = Dimens.SIZE_HALF.dp)) {
        itemsIndexed(state.controlledEvents) { index, item ->
            EventContent(item, userAction)
            if(index < state.controlledEvents.size - 1 ) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EventContent(competition: Competition, userAction: (CenterEffects) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().clickRipple{
            userAction.invoke(CenterEffects.OpenOrienteeringEventControl)
        }
    ) {
        Image(
            painter = painterResource(R.drawable.map_24dp),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Text(text = competition.title)
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Text(text = "Город: ${competition.address}")
                Text(text = "Дата: ${DateTimeFormat.formatDate(competition.date)}")
            }
        }
    }

}