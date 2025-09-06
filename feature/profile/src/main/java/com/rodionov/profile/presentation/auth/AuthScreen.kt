package com.rodionov.profile.presentation.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.clickRipple
import com.rodionov.profile.data.auth.AuthAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(authViewModel: AuthViewModel = koinViewModel()) {
    EmailInputContent(authViewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInputContent(userAction: (AuthAction) -> Unit) {
    bundleOf("te,p" to "a")
    var email by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Добавляем отступы по краям экрана
        contentAlignment = Alignment.Center // Центрирование содержимого Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DSTextInput(
                text = email,
                onValueChanged = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, // Устанавливаем тип клавиатуры для email
                    imeAction = ImeAction.Done // Действие на клавиатуре (например, "Готово")
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester) // Привязываем FocusRequester
            )

            Spacer(modifier = Modifier.height(16.dp)) // Отступ между полем и кнопкой

            Button(
                onClick = {
                    // TODO: Обработка введенного email
                    Log.d("LOG_TAG", "EmailInputContent: Введенный email: $email")
                    keyboardController?.hide()
                    userAction.invoke(AuthAction.AuthClicked(email))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отправить")
            }
        }
    }

    // Запрос фокуса после того, как компонент будет отрисован
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}