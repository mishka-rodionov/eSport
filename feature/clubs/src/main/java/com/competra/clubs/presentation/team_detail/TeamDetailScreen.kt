package com.competra.clubs.presentation.team_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.competra.clubs.data.team_detail.TeamDetailAction
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(teamId: String, viewModel: TeamDetailViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(teamId) { viewModel.initialize(teamId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.team?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(TeamDetailAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (state.isClubAdmin) {
                        IconButton(onClick = { viewModel.onAction(TeamDetailAction.OpenEditDialog) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit),
                                contentDescription = stringResource(R.string.club_detail_edit_action)
                            )
                        }
                        IconButton(onClick = { viewModel.onAction(TeamDetailAction.OpenDeleteConfirm) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.team_detail_delete_action)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.isClubAdmin) {
                FloatingActionButton(onClick = { viewModel.onAction(TeamDetailAction.OpenAddMemberDialog) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                        contentDescription = stringResource(R.string.team_detail_add_member_action)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(Dimens.SIZE_BASE.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.SIZE_BASE.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
                ) {
                    items(state.members, key = { it.id }) { member ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.SIZE_BASE.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${member.firstName} ${member.lastName}", fontWeight = FontWeight.Bold)
                                    Text(member.role.displayName(), style = MaterialTheme.typography.bodySmall)
                                }
                                if (state.isClubAdmin) {
                                    IconButton(
                                        onClick = {
                                            viewModel.onAction(TeamDetailAction.OpenRoleChangeDialog(member))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.edit),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isEditDialogOpen) {
        EditTeamDialog(
            name = state.editName,
            sportType = state.editSportType,
            isSaving = state.isEditSaving,
            onNameChanged = { viewModel.onAction(TeamDetailAction.EditNameChanged(it)) },
            onSportChanged = { viewModel.onAction(TeamDetailAction.EditSportChanged(it)) },
            onSave = { viewModel.onAction(TeamDetailAction.SaveEdit) },
            onDismiss = { viewModel.onAction(TeamDetailAction.CloseEditDialog) }
        )
    }

    if (state.isDeleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(TeamDetailAction.CloseDeleteConfirm) },
            title = { Text(stringResource(R.string.team_detail_delete_action)) },
            text = { Text(stringResource(R.string.club_detail_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(TeamDetailAction.ConfirmDelete) }) {
                    Text(stringResource(R.string.team_detail_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(TeamDetailAction.CloseDeleteConfirm) }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    if (state.isAddMemberDialogOpen) {
        AddMemberDialog(
            candidates = state.availableClubMembers,
            onSelect = { viewModel.onAction(TeamDetailAction.AddMember(it)) },
            onDismiss = { viewModel.onAction(TeamDetailAction.CloseAddMemberDialog) }
        )
    }

    state.roleChangeTarget?.let { member ->
        AlertDialog(
            onDismissRequest = { viewModel.onAction(TeamDetailAction.CloseRoleChangeDialog) },
            title = { Text("${member.firstName} ${member.lastName}") },
            text = {
                Column {
                    if (member.role == TeamRole.CAPTAIN) {
                        TextButton(
                            onClick = { viewModel.onAction(TeamDetailAction.ChangeMemberRole(member, TeamRole.MEMBER)) }
                        ) { Text(stringResource(R.string.team_role_member)) }
                    } else {
                        TextButton(
                            onClick = {
                                viewModel.onAction(TeamDetailAction.ChangeMemberRole(member, TeamRole.CAPTAIN))
                            }
                        ) { Text(stringResource(R.string.team_detail_make_captain_action)) }
                    }
                    TextButton(onClick = { viewModel.onAction(TeamDetailAction.RemoveMember(member)) }) {
                        Text(stringResource(R.string.team_detail_remove_member_action))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(TeamDetailAction.CloseRoleChangeDialog) }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun EditTeamDialog(
    name: String,
    sportType: KindOfSport,
    isSaving: Boolean,
    onNameChanged: (String) -> Unit,
    onSportChanged: (KindOfSport) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSportMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.team_form_edit_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.team_form_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                Box {
                    OutlinedButton(onClick = { isSportMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(sportType.name)
                    }
                    DropdownMenu(expanded = isSportMenuExpanded, onDismissRequest = { isSportMenuExpanded = false }) {
                        KindOfSport.all.forEach { sport ->
                            DropdownMenuItem(
                                text = { Text(sport.name) },
                                onClick = {
                                    onSportChanged(sport)
                                    isSportMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !isSaving && name.isNotBlank()) {
                Text(stringResource(R.string.team_form_save_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        }
    )
}

@Composable
private fun AddMemberDialog(candidates: List<ClubMember>, onSelect: (ClubMember) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.team_detail_add_member_action)) },
        text = {
            LazyColumn {
                items(candidates, key = { it.id }) { member ->
                    Text(
                        text = "${member.firstName} ${member.lastName}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(member) }
                            .padding(Dimens.SIZE_HALF.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        }
    )
}

@Composable
private fun TeamRole.displayName(): String = when (this) {
    TeamRole.CAPTAIN -> stringResource(R.string.team_role_captain)
    TeamRole.MEMBER -> stringResource(R.string.team_role_member)
}
