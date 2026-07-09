package com.competra.rating.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.Gender
import com.competra.rating.data.form.RatingFormAction
import com.competra.rating.data.form.RatingGroupDraft
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingFormScreen(clubId: String, viewModel: RatingFormViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(clubId) { viewModel.initialize(clubId) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.rating_form_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onAction(RatingFormAction.NameChanged(it)) },
                label = { Text(stringResource(R.string.rating_form_name_label)) },
                isError = state.nameError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.nameError) {
                Text(
                    text = stringResource(R.string.rating_form_name_required_error),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(text = stringResource(R.string.rating_form_groups_title), style = MaterialTheme.typography.titleMedium)

            state.groups.forEach { group ->
                RatingGroupEditor(
                    group = group,
                    onTitleChanged = { viewModel.onAction(RatingFormAction.GroupTitleChanged(group.localId, it)) },
                    onGenderChanged = { viewModel.onAction(RatingFormAction.GroupGenderChanged(group.localId, it)) },
                    onMinAgeChanged = { viewModel.onAction(RatingFormAction.GroupMinAgeChanged(group.localId, it)) },
                    onMaxAgeChanged = { viewModel.onAction(RatingFormAction.GroupMaxAgeChanged(group.localId, it)) },
                    onRemove = { viewModel.onAction(RatingFormAction.RemoveGroupClick(group.localId)) }
                )
            }

            if (state.groupsError) {
                Text(
                    text = stringResource(R.string.rating_form_groups_required_error),
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedButton(onClick = { viewModel.onAction(RatingFormAction.AddGroupClick) }) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px), contentDescription = null)
                Text(stringResource(R.string.rating_form_add_group_action))
            }

            Button(
                onClick = { viewModel.onAction(RatingFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.rating_form_save_action))
            }
        }
    }
}

@Composable
private fun RatingGroupEditor(
    group: RatingGroupDraft,
    onTitleChanged: (String) -> Unit,
    onGenderChanged: (Gender?) -> Unit,
    onMinAgeChanged: (String) -> Unit,
    onMaxAgeChanged: (String) -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp), verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = group.title,
                    onValueChange = onTitleChanged,
                    label = { Text(stringResource(R.string.rating_form_group_title_label)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = stringResource(R.string.rating_form_remove_group_action)
                    )
                }
            }

            GenderDropdown(selected = group.gender, onSelected = onGenderChanged)

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)) {
                OutlinedTextField(
                    value = group.minAge,
                    onValueChange = onMinAgeChanged,
                    label = { Text(stringResource(R.string.rating_form_group_min_age_label)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = group.maxAge,
                    onValueChange = onMaxAgeChanged,
                    label = { Text(stringResource(R.string.rating_form_group_max_age_label)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GenderDropdown(selected: Gender?, onSelected: (Gender?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (selected) {
        Gender.MALE -> stringResource(R.string.gender_male)
        Gender.FEMALE -> stringResource(R.string.gender_female)
        Gender.MIXED -> stringResource(R.string.gender_mixed)
        null -> stringResource(R.string.gender_any)
    }

    Column {
        TextButton(onClick = { expanded = true }) {
            Text("${stringResource(R.string.rating_form_group_gender_label)}: $label")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.gender_any)) },
                onClick = { onSelected(null); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.gender_male)) },
                onClick = { onSelected(Gender.MALE); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.gender_female)) },
                onClick = { onSelected(Gender.FEMALE); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.gender_mixed)) },
                onClick = { onSelected(Gender.MIXED); expanded = false }
            )
        }
    }
}
