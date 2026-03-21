package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.designsystem.theme.Dimens
import com.rodionov.data.navigation.CenterNavigation
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Четвертый экран создания соревнования: Настройка дистанций.
 * 
 * @param competitionId Идентификатор соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun CreateDistanceScreen(
    competitionId: Long,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = { 
                    viewModel.viewModelScope.launch {
                        viewModel.navigation.navigate(CenterNavigation.OrganizatorCompetitionFieldRoute(competitionId))
                    }
                },
                onNext = { viewModel.saveStepFour() },
                nextEnabled = state.distances.isNotEmpty()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
        ) {
            Text(
                text = "Шаг 4: Дистанции",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Список созданных дистанций
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
            ) {
                itemsIndexed(state.distances) { index, distance ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = distance.name ?: "Без названия", fontWeight = FontWeight.Bold)
                            Text(text = "Длина: ${distance.lengthMeters} м, КП: ${distance.controlsCount}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Button(
                onClick = { /* TODO: Открыть диалог создания дистанции */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить дистанцию")
            }
        }
    }
}
