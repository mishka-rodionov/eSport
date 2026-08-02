package com.competra.profile.presentation.registration

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.competra.designsystem.components.DSButton
import com.competra.designsystem.components.DSTextInput
import com.competra.profile.data.LegalLinks
import com.competra.profile.data.registration.RegistrationAction
import com.competra.resources.R
import com.competra.utils.DateTimeFormat
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
    val state by viewModel.state.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uriHandler = LocalUriHandler.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DSTextInput(
            text = state.firstName,
            onValueChanged = { userAction.invoke(RegistrationAction.UpdateFirstName(it)) },
            label = { Text(stringResource(R.string.label_first_name)) },
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
            text = state.lastName,
            onValueChanged = { userAction.invoke(RegistrationAction.UpdateLastName(it)) },
            label = { Text(stringResource(R.string.label_last_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DatePicker(DateTimeFormat.transformLongToDisplayDate(state.bdate)) { text -> userAction.invoke(RegistrationAction.UpdateBdate(text)) }

        Spacer(modifier = Modifier.height(16.dp))

        DSTextInput(
            text = state.email,
            onValueChanged = { userAction.invoke(RegistrationAction.UpdateEmail(it)) },
            label = { Text(stringResource(R.string.label_email)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.privacyAccepted,
                onCheckedChange = { userAction.invoke(RegistrationAction.UpdatePrivacyAccepted(it)) }
            )
            Text(
                text = buildString {
                    append(stringResource(R.string.label_privacy_consent_prefix))
                    append(" ")
                },
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.label_privacy_policy),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    uriHandler.openUri(LegalLinks.PRIVACY_POLICY_URL)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DSButton(
            text = stringResource(R.string.label_send),
            isEnabled = state.privacyAccepted,
            onClick = {
                keyboardController?.hide()
                userAction.invoke(RegistrationAction.RegisterUser)
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