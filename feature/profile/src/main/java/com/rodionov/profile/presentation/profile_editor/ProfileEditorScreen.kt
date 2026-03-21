package com.rodionov.profile.presentation.profile_editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.user.User
import org.koin.androidx.compose.koinViewModel

/**
 * Экран редактирования профиля пользователя.
 * Позволяет изменять основные данные пользователя и сохранять их локально и на сервере.
 *
 * @param viewModel Вьюмодель экрана.
 */
@Composable
fun ProfileEditorScreen(
    viewModel: ProfileEditorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ProfileEditorContent(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }
}

/**
 * Основной контент экрана редактирования профиля.
 *
 * @param state Состояние экрана.
 * @param onAction Обработчик действий пользователя.
 */
@Composable
private fun ProfileEditorContent(
    state: ProfileEditorState,
    onAction: (ProfileEditorAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val user = state.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Редактирование профиля",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Поля ввода для данных пользователя
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = user?.lastName ?: "",
            onValueChanged = { onAction(ProfileEditorAction.UpdateLastName(it)) },
            label = { Text("Фамилия") }
        )

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = user?.firstName ?: "",
            onValueChanged = { onAction(ProfileEditorAction.UpdateFirstName(it)) },
            label = { Text("Имя") }
        )

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = user?.middleName ?: "",
            onValueChanged = { onAction(ProfileEditorAction.UpdateMiddleName(it)) },
            label = { Text("Отчество") }
        )

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = user?.phoneNumber ?: "",
            onValueChanged = { onAction(ProfileEditorAction.UpdatePhoneNumber(it)) },
            label = { Text("Номер телефона") }
        )

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = user?.email ?: "",
            onValueChanged = { onAction(ProfileEditorAction.UpdateEmail(it)) },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка сохранения изменений
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = { onAction(ProfileEditorAction.SaveProfile) },
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Сохранить", fontWeight = FontWeight.Bold)
            }
        }

        // Отображение ошибки, если есть
        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditorPreview() {
    MaterialTheme {
        ProfileEditorContent(
            state = ProfileEditorState(
                user = User(
                    id = "1",
                    firstName = "Иван",
                    lastName = "Иванов",
                    middleName = "Иванович",
                    birthDate = 0L,
                    gender = Gender.MALE,
                    photo = "",
                    phoneNumber = "+79991234567",
                    email = "ivanov@example.com",
                    qualification = emptyList()
                )
            ),
            onAction = {}
        )
    }
}
