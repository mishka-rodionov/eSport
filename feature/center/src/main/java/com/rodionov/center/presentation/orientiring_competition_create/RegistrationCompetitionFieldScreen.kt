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
 * Второй экран создания соревнования: Регистрация.
 * 
 * @param competitionId Идентификатор соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun RegistrationCompetitionFieldScreen(
    competitionId: Long,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    RegistrationCompetitionFieldContent(
        state = state,
        onBack = viewModel::back,
        onNext = viewModel::saveStepTwo,
        onUpdateMaxParticipants = viewModel::updateMaxParticipants,
        onUpdateFeeAmount = viewModel::updateFeeAmount,
        onUpdateRegulationUrl = viewModel::updateRegulationUrl
    )
}

/**
 * Контент экрана параметров регистрации.
 * Выделен отдельно для поддержки Preview.
 */
@Composable
private fun RegistrationCompetitionFieldContent(
    state: OrienteeringCreatorState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onUpdateMaxParticipants: (String) -> Unit,
    onUpdateFeeAmount: (String) -> Unit,
    onUpdateRegulationUrl: (String) -> Unit
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
                text = "Шаг 2: Параметры регистрации",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поля даты регистрации (заглушки для DSTextInput)
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.registrationStart?.toString() ?: "",
                label = { Text("Начало регистрации") },
                onValueChanged = { /* Использование закомментировано до реализации */ }
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.maxParticipants?.toString() ?: "",
                label = { Text("Лимит участников") },
                onValueChanged = onUpdateMaxParticipants
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DSTextInput(
                    modifier = Modifier.weight(1f),
                    text = state.feeAmount?.toString() ?: "",
                    label = { Text("Взнос") },
                    onValueChanged = onUpdateFeeAmount
                )
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                DSTextInput(
                    modifier = Modifier.weight(0.5f),
                    text = state.feeCurrency,
                    label = { Text("Валюта") },
                    onValueChanged = { /* viewModel.updateCurrency(it) */ }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.regulationUrl,
                label = { Text("Ссылка на регламент") },
                onValueChanged = onUpdateRegulationUrl
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationCompetitionFieldPreview() {
    MaterialTheme {
        RegistrationCompetitionFieldContent(
            state = OrienteeringCreatorState(
                maxParticipants = 200,
                feeAmount = 500.0,
                feeCurrency = "RUB",
                regulationUrl = "https://example.com/rules"
            ),
            onBack = {},
            onNext = {},
            onUpdateMaxParticipants = {},
            onUpdateFeeAmount = {},
            onUpdateRegulationUrl = {}
        )
    }
}
