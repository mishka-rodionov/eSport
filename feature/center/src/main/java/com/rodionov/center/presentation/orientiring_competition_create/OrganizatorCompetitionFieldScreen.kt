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
                    //    viewModel.navigation.navigate(CenterNavigation.RegistrationCompetitionFieldRoute(competitionId))
                    // }
                },
                onNext = { viewModel.saveStepThree() }
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
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.mapUrl,
                label = { Text("Ссылка на карту") },
                onValueChanged = viewModel::updateMapUrl
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.contactPhone,
                label = { Text("Контактный телефон") },
                onValueChanged = viewModel::updateContactPhone
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.contactEmail,
                label = { Text("Контактный Email") },
                onValueChanged = viewModel::updateContactEmail
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.website,
                label = { Text("Официальный сайт") },
                onValueChanged = viewModel::updateWebsite
            )
        }
    }
}
