package com.competra.clubs.presentation.detail

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.competra.clubs.data.detail.ClubDetailAction
import com.competra.clubs.data.detail.ClubDetailTab
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.models.club.Team
import com.competra.resources.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(clubId: String, viewModel: ClubDetailViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(clubId) { viewModel.initialize(clubId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.club?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(ClubDetailAction.BackClick) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onAction(ClubDetailAction.OpenRatings) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_star_24px),
                            contentDescription = stringResource(R.string.club_detail_ratings_action)
                        )
                    }
                    if (state.isAdmin) {
                        IconButton(onClick = { viewModel.onAction(ClubDetailAction.OpenJoinRequests) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_check_24px),
                                contentDescription = stringResource(R.string.club_detail_join_requests_action)
                            )
                        }
                        IconButton(onClick = { viewModel.onAction(ClubDetailAction.OpenEditDialog) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit),
                                contentDescription = stringResource(R.string.club_detail_edit_action)
                            )
                        }
                    }
                    if (state.isFounder) {
                        IconButton(onClick = { viewModel.onAction(ClubDetailAction.OpenDeleteConfirm) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.club_detail_delete_action)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val club = state.club
            if (club != null) {
                Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
                    club.description?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }

                    when {
                        state.myMembership != null -> {
                            if (!state.isFounder) {
                                OutlinedButton(onClick = { viewModel.onAction(ClubDetailAction.LeaveClub) }) {
                                    Text(stringResource(R.string.club_detail_leave_action))
                                }
                            }
                        }
                        state.myPendingJoinRequest != null -> {
                            val requestStatus = state.myPendingJoinRequest?.status
                            Text(
                                text = when (requestStatus?.name) {
                                    "REJECTED" -> stringResource(R.string.club_detail_join_request_rejected)
                                    else -> stringResource(R.string.club_detail_join_request_pending)
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        club.allowJoinRequests -> {
                            Button(onClick = { viewModel.onAction(ClubDetailAction.JoinClub) }) {
                                Text(stringResource(R.string.club_detail_join_action))
                            }
                        }
                    }
                }
            }

            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                Tab(
                    selected = state.selectedTab == ClubDetailTab.MEMBERS,
                    onClick = { viewModel.onAction(ClubDetailAction.SelectTab(ClubDetailTab.MEMBERS)) },
                    text = { Text(stringResource(R.string.club_detail_members_tab)) }
                )
                Tab(
                    selected = state.selectedTab == ClubDetailTab.TEAMS,
                    onClick = { viewModel.onAction(ClubDetailAction.SelectTab(ClubDetailTab.TEAMS)) },
                    text = { Text(stringResource(R.string.club_detail_teams_tab)) }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (state.selectedTab) {
                    ClubDetailTab.MEMBERS -> MembersTab(
                        members = state.members,
                        isAdmin = state.isAdmin,
                        myUserId = state.myUserId,
                        onMemberClick = { viewModel.onAction(ClubDetailAction.OpenRoleChangeDialog(it)) }
                    )
                    ClubDetailTab.TEAMS -> TeamsTab(
                        teams = state.teams,
                        isAdmin = state.isAdmin,
                        onTeamClick = { viewModel.onAction(ClubDetailAction.TeamClick(it)) },
                        onCreateTeamClick = { viewModel.onAction(ClubDetailAction.OpenCreateTeamDialog) }
                    )
                }
            }
        }
    }

    if (state.isEditDialogOpen) {
        EditClubDialog(
            name = state.editName,
            description = state.editDescription,
            allowJoinRequests = state.editAllowJoinRequests,
            isSaving = state.isEditSaving,
            onNameChanged = { viewModel.onAction(ClubDetailAction.EditNameChanged(it)) },
            onDescriptionChanged = { viewModel.onAction(ClubDetailAction.EditDescriptionChanged(it)) },
            onAllowJoinRequestsChanged = { viewModel.onAction(ClubDetailAction.EditAllowJoinRequestsChanged(it)) },
            onSave = { viewModel.onAction(ClubDetailAction.SaveEdit) },
            onDismiss = { viewModel.onAction(ClubDetailAction.CloseEditDialog) }
        )
    }

    if (state.isDeleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ClubDetailAction.CloseDeleteConfirm) },
            title = { Text(stringResource(R.string.club_detail_delete_confirm_title)) },
            text = { Text(stringResource(R.string.club_detail_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(ClubDetailAction.ConfirmDelete) }) {
                    Text(stringResource(R.string.club_detail_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ClubDetailAction.CloseDeleteConfirm) }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    if (state.isCreateTeamDialogOpen) {
        CreateTeamDialog(
            name = state.createTeamName,
            sportType = state.createTeamSportType,
            isSaving = state.isCreateTeamSaving,
            onNameChanged = { viewModel.onAction(ClubDetailAction.CreateTeamNameChanged(it)) },
            onSportChanged = { viewModel.onAction(ClubDetailAction.CreateTeamSportChanged(it)) },
            onSave = { viewModel.onAction(ClubDetailAction.SaveCreateTeam) },
            onDismiss = { viewModel.onAction(ClubDetailAction.CloseCreateTeamDialog) }
        )
    }

    state.roleChangeTarget?.let { member ->
        RoleChangeDialog(
            member = member,
            isFounder = state.isFounder,
            onChangeRole = { role -> viewModel.onAction(ClubDetailAction.ChangeMemberRole(member, role)) },
            onRemove = { viewModel.onAction(ClubDetailAction.RemoveMember(member)) },
            onDismiss = { viewModel.onAction(ClubDetailAction.CloseRoleChangeDialog) }
        )
    }
}

@Composable
private fun MembersTab(
    members: List<ClubMember>,
    isAdmin: Boolean,
    myUserId: String?,
    onMemberClick: (ClubMember) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SIZE_BASE.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
    ) {
        items(members, key = { it.id }) { member ->
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
                    if (isAdmin && member.userId != myUserId) {
                        IconButton(onClick = { onMemberClick(member) }) {
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

@Composable
private fun TeamsTab(teams: List<Team>, isAdmin: Boolean, onTeamClick: (String) -> Unit, onCreateTeamClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (isAdmin) {
            Button(
                onClick = onCreateTeamClick,
                modifier = Modifier.fillMaxWidth().padding(Dimens.SIZE_BASE.dp)
            ) {
                Text(stringResource(R.string.club_detail_create_team_action))
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Dimens.SIZE_BASE.dp, vertical = Dimens.SIZE_HALF.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
        ) {
            items(teams, key = { it.id }) { team ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SIZE_BASE.dp)
                    ) {
                        Text(team.name, fontWeight = FontWeight.Bold)
                        Text(
                            "${team.sportType.name} · ${team.membersCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { onTeamClick(team.id) }) {
                            Text(stringResource(R.string.club_detail_members_tab))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditClubDialog(
    name: String,
    description: String,
    allowJoinRequests: Boolean,
    isSaving: Boolean,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAllowJoinRequestsChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.club_form_edit_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.club_form_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    label = { Text(stringResource(R.string.club_form_description_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.club_form_allow_join_requests_label),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = allowJoinRequests, onCheckedChange = onAllowJoinRequestsChanged)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !isSaving) {
                Text(stringResource(R.string.club_form_save_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        }
    )
}

@Composable
private fun CreateTeamDialog(
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
        title = { Text(stringResource(R.string.team_form_create_title)) },
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
private fun RoleChangeDialog(
    member: ClubMember,
    isFounder: Boolean,
    onChangeRole: (ClubRole) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${member.firstName} ${member.lastName}") },
        text = {
            Column {
                if (isFounder) {
                    TextButton(onClick = { onChangeRole(ClubRole.FOUNDER) }) {
                        Text(stringResource(R.string.club_detail_transfer_founder_action))
                    }
                    if (member.role == ClubRole.ADMIN) {
                        TextButton(onClick = { onChangeRole(ClubRole.MEMBER) }) {
                            Text(stringResource(R.string.club_detail_remove_admin_action))
                        }
                    } else {
                        TextButton(onClick = { onChangeRole(ClubRole.ADMIN) }) {
                            Text(stringResource(R.string.club_detail_make_admin_action))
                        }
                    }
                }
                TextButton(onClick = onRemove) {
                    Text(stringResource(R.string.club_detail_remove_member_action))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) }
        }
    )
}

@Composable
private fun ClubRole.displayName(): String = when (this) {
    ClubRole.FOUNDER -> stringResource(R.string.club_role_founder)
    ClubRole.ADMIN -> stringResource(R.string.club_role_admin)
    ClubRole.MEMBER -> stringResource(R.string.club_role_member)
}
