package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorState
import org.koin.androidx.compose.koinViewModel

/**
 * Третий экран создания соревнования: Информация об организаторе.
 * 
 * @param competitionId Идентификатор соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun OrganizatorCompetitionFieldScreen(
    competitionId: Long,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    OrganizatorCompetitionFieldContent(
        state = state,
        onBack = viewModel::back,
        onNext = viewModel::saveStepThree,
        onUpdateMapUrl = viewModel::updateMapUrl,
        onUpdateContactPhone = viewModel::updateContactPhone,
        onUpdateContactEmail = viewModel::updateContactEmail,
        onUpdateWebsite = viewModel::updateWebsite
    )
}

/**
 * Контент экрана контактов и медиа.
 * Выделен отдельно для поддержки Preview.
 */
@Composable
private fun OrganizatorCompetitionFieldContent(
    state: OrienteeringCreatorState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onUpdateMapUrl: (String) -> Unit,
    onUpdateContactPhone: (String) -> Unit,
    onUpdateContactEmail: (String) -> Unit,
    onUpdateWebsite: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = onBack,
                onNext = onNext
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Шаг 3: Контакты и медиа",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (false) { //на данный момент отключено
                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.mapUrl,
                    label = { Text("Ссылка на карту") },
                    onValueChanged = onUpdateMapUrl
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.contactPhone,
                label = { Text("Контактный телефон") },
                onValueChanged = onUpdateContactPhone
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.contactEmail,
                label = { Text("Контактный Email") },
                onValueChanged = onUpdateContactEmail
            )

            if (false) { // на данный момент отключено
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.website,
                    label = { Text("Официальный сайт") },
                    onValueChanged = onUpdateWebsite
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrganizatorCompetitionFieldPreview() {
    MaterialTheme {
        OrganizatorCompetitionFieldContent(
            state = OrienteeringCreatorState(
                mapUrl = "https://example.com/map",
                contactPhone = "+79991234567",
                contactEmail = "org@example.com",
                website = "https://example.com"
            ),
            onBack = {},
            onNext = {},
            onUpdateMapUrl = {},
            onUpdateContactPhone = {},
            onUpdateContactEmail = {},
            onUpdateWebsite = {}
        )
    }
}
