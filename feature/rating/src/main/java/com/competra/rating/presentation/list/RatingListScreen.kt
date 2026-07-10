package com.competra.rating.presentation.list

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.rating.RatingSeries
import com.competra.rating.data.list.RatingListAction
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingListScreen(clubId: String, viewModel: RatingListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(clubId) { viewModel.initialize(clubId) }

    // LocalLifecycleOwner здесь — lifecycle конкретного NavBackStackEntry этого роута,
    // а не Activity, поэтому ON_RESUME срабатывает и при возврате из деталей рейтинга
    // (Compose Navigation не перевызывает composable-лямбду при pop back stack).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rating_list_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(RatingListAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(onClick = { viewModel.onAction(RatingListAction.CreateRatingClick) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                        contentDescription = stringResource(R.string.rating_list_create_action)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.ratings.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.ratings.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.rating_list_empty),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
                        contentPadding = PaddingValues(
                            horizontal = Dimens.SIZE_BASE.dp,
                            vertical = Dimens.SIZE_HALF.dp
                        )
                    ) {
                        items(state.ratings, key = { it.id }) { rating ->
                            RatingItem(
                                rating = rating,
                                onClick = { viewModel.onAction(RatingListAction.RatingClick(rating.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingItem(rating: RatingSeries, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
            Text(text = rating.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}
