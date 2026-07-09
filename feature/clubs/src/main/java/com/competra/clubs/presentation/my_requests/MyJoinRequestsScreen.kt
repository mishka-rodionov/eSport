package com.competra.clubs.presentation.my_requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.competra.clubs.data.my_requests.MyJoinRequestsAction
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.club.ClubJoinRequest
import com.competra.domain.models.club.JoinRequestStatus
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJoinRequestsScreen(viewModel: MyJoinRequestsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_join_requests_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(MyJoinRequestsAction.BackClick) }) {
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
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.SIZE_BASE.dp))
                state.requests.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.my_join_requests_empty),
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
                            RequestItem(
                                request = request,
                                clubName = state.clubNames[request.clubId].orEmpty(),
                                onClick = { viewModel.onAction(MyJoinRequestsAction.RequestClick(request.clubId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestItem(request: ClubJoinRequest, clubName: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SIZE_BASE.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = clubName.ifBlank { request.clubId }, fontWeight = FontWeight.Bold)
            Text(text = request.status.displayName(), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun JoinRequestStatus.displayName(): String = when (this) {
    JoinRequestStatus.PENDING -> stringResource(R.string.join_request_status_pending)
    JoinRequestStatus.APPROVED -> stringResource(R.string.join_request_status_approved)
    JoinRequestStatus.REJECTED -> stringResource(R.string.join_request_status_rejected)
}
