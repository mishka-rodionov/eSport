package com.competra.clubs.presentation.join_requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import com.competra.clubs.data.join_requests.ClubJoinRequestsAction
import com.competra.designsystem.theme.Dimens
import androidx.compose.ui.unit.dp
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubJoinRequestsScreen(clubId: String, viewModel: ClubJoinRequestsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(clubId) { viewModel.initialize(clubId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.club_join_requests_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(ClubJoinRequestsAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(Dimens.SIZE_BASE.dp))
                }
                state.requests.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.club_join_requests_empty),
                        modifier = Modifier.padding(Dimens.SIZE_BASE.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Dimens.SIZE_BASE.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
                    ) {
                        items(state.requests, key = { it.id }) { request ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimens.SIZE_BASE.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${request.firstName} ${request.lastName}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Row {
                                        TextButton(
                                            onClick = {
                                                viewModel.onAction(ClubJoinRequestsAction.Reject(request.id))
                                            }
                                        ) {
                                            Text(stringResource(R.string.club_join_request_reject))
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.onAction(ClubJoinRequestsAction.Approve(request.id))
                                            }
                                        ) {
                                            Text(stringResource(R.string.club_join_request_approve))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
