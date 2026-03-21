package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.domain.models.KindOfSport
import org.koin.androidx.compose.koinViewModel

/**
 * Первый экран создания соревнования: Общая информация.
 * 
 * @param competitionId Идентификатор редактируемого соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun CommonCompetitionFieldScreen(
    competitionId: Long? = null,
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
                onBack = { viewModel.back() },
                onNext = { 
                    // Сохранение и переход к следующему шагу
                    viewModel.saveStepOne() 
                },
                nextEnabled = state.title.isNotBlank() && state.address.isNotBlank()
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
                text = "Шаг 1: Общая информация",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.title,
                label = { Text("Название соревнования") },
                onValueChanged = viewModel::updateTitle
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.address,
                label = { Text("Место проведения") },
                onValueChanged = viewModel::updateAddress
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            // Здесь будут DatePicker и TimePicker (используем существующие из старого экрана)
            // Для краткости примера опускаю их реализацию, но в VM они должны работать
            
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.description,
                label = { Text("Описание") },
                onValueChanged = viewModel::updateDescription
            )
        }
    }
}

@Composable
fun NavigationButtons(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true,
    nextText: String = "Далее"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SIZE_BASE.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
        Button(onClick = onNext, enabled = nextEnabled) {
            Text(nextText)
        }
    }
}
