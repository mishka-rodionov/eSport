package com.rodionov.profile.presentation.registration

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import com.example.designsystem.components.DSTextInput
import com.rodionov.profile.data.RegistrationAction
import com.rodionov.profile.data.auth.AuthAction
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = koinViewModel()) {

    val userAction = remember { viewModel::onAction }

    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var bdate by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DSTextInput(
            text = firstName,
            onValueChanged = { firstName = it },
            label = { Text("Имя") },
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

        DSTextInput(
            text = lastName,
            onValueChanged = { lastName = it },
            label = { Text("Фамилия") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DSTextInput(
            text = bdate,
            onValueChanged = { bdate = it },
            label = { Text("Дата рождения") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DSTextInput(
            text = email,
            onValueChanged = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // TODO: Обработка введенного email
                Log.d("LOG_TAG", "EmailInputContent: Введенный email: $email")
                keyboardController?.hide()
                userAction.invoke(
                    RegistrationAction.RegisterUser(
                        firstName,
                        lastName,
                        bdate,
                        email
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }
    }

    // Запрос фокуса после того, как компонент будет отрисован
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}