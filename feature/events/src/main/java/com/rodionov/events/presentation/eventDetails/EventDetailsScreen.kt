package com.rodionov.events.presentation.eventDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rodionov.events.presentation.main.DetailsInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventDetailsScreen(viewModel: EventDetailsViewModel = koinViewModel()) {
    Column {
        Text(text = "Event Details ${viewModel.detailInfo?.title}")
        Text(text = "Event Details ${viewModel.detailInfo?.description}")
    }
}