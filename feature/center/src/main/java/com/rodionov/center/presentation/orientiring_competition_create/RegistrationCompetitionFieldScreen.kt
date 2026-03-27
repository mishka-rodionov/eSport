package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.data.navigation.CenterNavigation
import kotlinx.coroutines.launch
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
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = { 
                    // Исправлено: вместо навигации на конкретный роут вызываем возврат назад по стеку
                    viewModel.back()
                    // viewModel.viewModelScope.launch {
                    //    viewModel.navigation.navigate(CenterNavigation.CommonCompetitionFieldRoute(competitionId))
                    // }
                },
                onNext = { viewModel.saveStepTwo() }
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
                onValueChanged = { /* viewModel.updateRegistrationStart(...) */ }
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.maxParticipants?.toString() ?: "",
                label = { Text("Лимит участников") },
                onValueChanged = viewModel::updateMaxParticipants
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DSTextInput(
                    modifier = Modifier.weight(1f),
                    text = state.feeAmount?.toString() ?: "",
                    label = { Text("Взнос") },
                    onValueChanged = viewModel::updateFeeAmount
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
                onValueChanged = viewModel::updateRegulationUrl
            )
        }
    }
}
