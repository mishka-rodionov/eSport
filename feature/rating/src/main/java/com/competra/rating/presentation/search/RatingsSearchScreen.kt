package com.competra.rating.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.rating.RatingSummary
import com.competra.rating.data.search.RatingsSearchAction
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsSearchScreen(viewModel: RatingsSearchViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ratings_search_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(RatingsSearchAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onAction(RatingsSearchAction.QueryChanged(it)) },
                label = { Text(stringResource(R.string.ratings_search_hint)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SIZE_BASE.dp, vertical = Dimens.SIZE_HALF.dp)
            )

            if (state.ratings.isEmpty() && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.ratings_search_empty))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.SIZE_BASE.dp,
                        vertical = Dimens.SIZE_HALF.dp
                    )
                ) {
                    items(state.ratings, key = { it.id }) { rating ->
                        RatingSummaryItem(
                            rating = rating,
                            onClick = { viewModel.onAction(RatingsSearchAction.RatingClick(rating.id)) }
                        )
                    }
                    if (state.hasMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.padding(Dimens.SIZE_BASE.dp))
                                } else {
                                    Button(onClick = { viewModel.onAction(RatingsSearchAction.LoadMore) }) {
                                        Text(stringResource(R.string.clubs_load_more))
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

@Composable
private fun RatingSummaryItem(rating: RatingSummary, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
            Text(text = rating.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = rating.ownerClubName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
