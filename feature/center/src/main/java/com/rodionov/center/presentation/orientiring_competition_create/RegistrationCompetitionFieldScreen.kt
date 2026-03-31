package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
        onUpdateFeeEnabled = viewModel::updateFeeEnabled,
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
    onUpdateFeeEnabled: (Boolean) -> Unit,
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
            Text(
                text = "Начало регистрации",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    DatePicker(state = state, userAction = {  })
                }
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                Box(modifier = Modifier.weight(1f)) {
                    TimePicker(state = state, userAction = {  })
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поля даты регистрации (заглушки для DSTextInput)
            Text(
                text = "Окончание регистрации",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    DatePicker(state = state, userAction = {})
                }
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                Box(modifier = Modifier.weight(1f)) {
                    TimePicker(state = state, userAction = {  })
                }
            }

            if (false) { // на данном этапе отключено
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.maxParticipants?.toString() ?: "",
                    label = { Text("Лимит участников") },
                    onValueChanged = onUpdateMaxParticipants
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Секция взноса со свитчем
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Платное участие",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = state.isFeeEnabled,
                    onCheckedChange = onUpdateFeeEnabled
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DSTextInput(
                    modifier = Modifier.weight(1f),
                    text = state.feeAmount?.toString() ?: "",
                    label = { Text("Взнос") },
                    enabled = state.isFeeEnabled, // Активно только если включен свитч
                    onValueChanged = onUpdateFeeAmount
                )
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                DSTextInput(
                    modifier = Modifier.weight(0.5f),
                    text = state.feeCurrency,
                    label = { Text("Валюта") },
                    enabled = state.isFeeEnabled, // Активно только если включен свитч
                    onValueChanged = { /* viewModel.updateCurrency(it) */ }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            if (false) { //на данном этапе отключено
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.regulationUrl,
                    label = { Text("Ссылка на регламент") },
                    onValueChanged = onUpdateRegulationUrl
                )
            }
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
                isFeeEnabled = true,
                feeAmount = 500.0,
                feeCurrency = "RUB",
                regulationUrl = "https://example.com/rules"
            ),
            onBack = {},
            onNext = {},
            onUpdateMaxParticipants = {},
            onUpdateFeeEnabled = {},
            onUpdateFeeAmount = {},
            onUpdateRegulationUrl = {}
        )
    }
}
