package com.competra.rating.presentation.group_mapping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.rating.RatingGroup
import com.competra.domain.models.rating.RatingGroupMappingSuggestion
import com.competra.rating.data.group_mapping.GroupMappingAction
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupMappingScreen(
    ratingId: String,
    competitionId: String,
    viewModel: GroupMappingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(ratingId, competitionId) { viewModel.initialize(ratingId, competitionId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rating_group_mapping_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(GroupMappingAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
        ) {
            Text(text = stringResource(R.string.rating_group_mapping_hint), style = MaterialTheme.typography.bodyMedium)

            state.suggestions.forEach { suggestion ->
                MappingRow(
                    suggestion = suggestion,
                    ratingGroups = state.ratingGroups,
                    selectedRatingGroupId = state.mapping[suggestion.participantGroupId],
                    onSelected = { viewModel.onAction(GroupMappingAction.MappingChanged(suggestion.participantGroupId, it)) }
                )
            }

            Button(
                onClick = { viewModel.onAction(GroupMappingAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.rating_group_mapping_save_action))
            }
        }
    }
}

@Composable
private fun MappingRow(
    suggestion: RatingGroupMappingSuggestion,
    ratingGroups: List<RatingGroup>,
    selectedRatingGroupId: Long?,
    onSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = ratingGroups.firstOrNull { it.id == selectedRatingGroupId }?.title
        ?: stringResource(R.string.rating_group_mapping_no_match)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
            Text(text = suggestion.participantGroupTitle, style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = { expanded = true }) {
                Text(selectedTitle)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.rating_group_mapping_no_match)) },
                    onClick = { onSelected(null); expanded = false }
                )
                ratingGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.title) },
                        onClick = { onSelected(group.id); expanded = false }
                    )
                }
            }
        }
    }
}
