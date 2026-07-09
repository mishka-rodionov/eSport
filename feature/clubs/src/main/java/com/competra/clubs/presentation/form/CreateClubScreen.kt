package com.competra.clubs.presentation.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.competra.clubs.data.form.ClubFormAction
import com.competra.designsystem.theme.Dimens
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClubScreen(viewModel: ClubFormViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.club_form_create_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onAction(ClubFormAction.NameChanged(it)) },
                label = { Text(stringResource(R.string.club_form_name_label)) },
                isError = state.nameError,
                supportingText = {
                    if (state.nameError) Text(stringResource(R.string.club_form_name_required_error))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onAction(ClubFormAction.DescriptionChanged(it)) },
                label = { Text(stringResource(R.string.club_form_description_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.club_form_allow_join_requests_label),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.allowJoinRequests,
                    onCheckedChange = { viewModel.onAction(ClubFormAction.AllowJoinRequestsChanged(it)) }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Button(
                onClick = { viewModel.onAction(ClubFormAction.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
                } else {
                    Text(stringResource(R.string.club_form_save_action))
                }
            }
        }
    }
}
