package com.competra.rating.presentation.detail

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.rating.RatingCompetition
import com.competra.domain.models.rating.RatingStanding
import com.competra.rating.data.detail.RatingDetailAction
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDetailScreen(ratingId: String, viewModel: RatingDetailViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(ratingId) { viewModel.initialize(ratingId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.rating?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(RatingDetailAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (state.isAdmin) {
                        IconButton(onClick = { viewModel.onAction(RatingDetailAction.AddCompetitionClick) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                                contentDescription = stringResource(R.string.rating_detail_add_competition_action)
                            )
                        }
                        IconButton(onClick = { viewModel.onAction(RatingDetailAction.OpenDeleteConfirm) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.rating_detail_delete_action)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val rating = state.rating
        if (state.isLoading && rating == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        if (rating == null) return@Scaffold

        if (rating.groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.rating_detail_no_groups))
            }
            return@Scaffold
        }

        // Единый скроллящийся список: иначе при длинной таблице результатов раздел
        // "Соревнования рейтинга" уходит за пределы экрана и становится недостижим.
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = Dimens.SIZE_BASE.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_QUARTER.dp)
        ) {
            item {
                ScrollableTabRow(selectedTabIndex = rating.groups.indexOfFirst { it.id == state.selectedGroupId }.coerceAtLeast(0)) {
                    rating.groups.forEach { group ->
                        Tab(
                            selected = group.id == state.selectedGroupId,
                            onClick = { viewModel.onAction(RatingDetailAction.SelectGroup(group.id)) },
                            text = { Text(group.title) }
                        )
                    }
                }
            }

            if (state.isStandingsLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(Dimens.SIZE_BASE.dp))
                    }
                }
            } else if (state.standings.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(Dimens.SIZE_BASE.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.rating_detail_standings_empty))
                    }
                }
            } else {
                items(state.standings, key = { "standing_${it.participantKey}" }) { standing ->
                    Box(modifier = Modifier.padding(horizontal = Dimens.SIZE_BASE.dp)) {
                        StandingRow(standing)
                    }
                }
            }

            if (state.competitions.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.rating_detail_competitions_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Dimens.SIZE_BASE.dp)
                    )
                }
                items(state.competitions, key = { "competition_${it.id}" }) { competition ->
                    Box(modifier = Modifier.padding(horizontal = Dimens.SIZE_BASE.dp)) {
                        CompetitionRow(
                            competition = competition,
                            isAdmin = state.isAdmin,
                            onRemove = { viewModel.onAction(RatingDetailAction.RemoveCompetitionClick(competition.competitionId)) },
                            onEditMapping = { viewModel.onAction(RatingDetailAction.EditMappingClick(competition.competitionId)) }
                        )
                    }
                }
            }
        }
    }

    if (state.isDeleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(RatingDetailAction.CloseDeleteConfirm) },
            title = { Text(stringResource(R.string.rating_detail_delete_confirm_title)) },
            text = { Text(stringResource(R.string.rating_detail_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(RatingDetailAction.ConfirmDelete) }) {
                    Text(stringResource(R.string.rating_detail_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(RatingDetailAction.CloseDeleteConfirm) }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun StandingRow(standing: RatingStanding) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.SIZE_BASE.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${standing.rank}. ${standing.displayName}", fontWeight = FontWeight.Bold)
            Text(text = "${standing.totalPoints}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun CompetitionRow(
    competition: RatingCompetition,
    isAdmin: Boolean,
    onRemove: () -> Unit,
    onEditMapping: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SIZE_QUARTER.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = competition.competitionTitle, modifier = Modifier.weight(1f))
        if (isAdmin) {
            IconButton(onClick = onEditMapping) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = stringResource(R.string.rating_detail_edit_mapping_action)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.delete),
                    contentDescription = stringResource(R.string.rating_detail_remove_competition_action)
                )
            }
        }
    }
}
