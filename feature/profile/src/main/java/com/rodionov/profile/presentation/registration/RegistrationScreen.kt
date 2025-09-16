package com.rodionov.profile.presentation.registration

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSButton
import com.example.designsystem.components.DSTextInput
import com.rodionov.profile.data.RegistrationAction
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.util.Calendar

/**
 * Экран ввода регистрационных данных
 * [email] - адрес электронной почты
 * [firstName] - имя
 * [lastName] - фамилия
 * [bdate] - дата рождения
 * */
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = koinViewModel()) {

    val userAction = remember { viewModel::onAction }

    var email by remember { mutableStateOf("mishka727@yandex.ru") }
    var firstName by remember { mutableStateOf("Михаил") }
    var lastName by remember { mutableStateOf("Родионов") }
    var bdate by remember { mutableStateOf("06.04.1989") }
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

        DatePicker(bdate) { text -> bdate = text }

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

        DSButton(
            text = "Отправить",
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
        )
    }

    // Запрос фокуса после того, как компонент будет отрисован
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DatePicker(bdate: String, userAction: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                userAction.invoke( DateTimeFormat.formatDate(date))
                focusManager.clearFocus()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    datePickerDialog.setOnDismissListener { focusManager.clearFocus() }

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    datePickerDialog.show()
                }
            },
        label = {
            Text(text = stringResource(R.string.label_date))
        },
        text = bdate,
        interactionSource = interactionSource,
        enabled = true,
        readOnly = true
    )

}