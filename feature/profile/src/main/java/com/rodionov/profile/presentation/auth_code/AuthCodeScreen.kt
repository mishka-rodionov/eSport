package com.rodionov.profile.presentation.auth_code

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.key // Используем event.key
import androidx.compose.ui.input.key.type
import com.rodionov.profile.data.auth.AuthAction
import org.koin.compose.viewmodel.koinViewModel


const val OTP_LENGTH = 6

@Composable
fun AuthCodeScreen(viewModel: AuthCodeViewModel = koinViewModel()) {
    OtpInputContent(viewModel::onAction)
}

@OptIn(ExperimentalComposeUiApi::class) // Нужен для onKeyEvent
@Composable
fun OtpInputContent(userAction: (AuthAction) -> Unit) {
    val otpValues = remember { mutableStateListOf<String>().apply { repeat(OTP_LENGTH) { add("") } } }
    val focusRequesters = remember { List(OTP_LENGTH) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    fun getFullOtp(): String = otpValues.joinToString("")

    fun onOtpValueChanged(index: Int, newValue: String) {
        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
            otpValues[index] = newValue
            if (newValue.isNotEmpty() && index < OTP_LENGTH - 1) {
                focusRequesters[index + 1].requestFocus()
            }
            // Не перемещаем фокус назад при удалении здесь, это делается в onKeyEvent

            if (otpValues.all { it.isNotEmpty() }) {
                focusManager.clearFocus()
                Log.d("LOG_TAG", "onOtpValueChanged: Введенный OTP: ${getFullOtp()}")
                userAction.invoke(AuthAction.AuthCodeEntered(getFullOtp()))
                // TODO: Здесь можно вызвать функцию для проверки OTP
            }
        } else if (newValue.isEmpty()) { // Обработка случая, когда значение стало пустым (например, из-за Backspace)
            otpValues[index] = ""
            // Логика перемещения фокуса назад при удалении обрабатывается в onKeyEvent
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Введите код из СМС", fontSize = 20.sp, modifier = Modifier.padding(bottom = 24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until OTP_LENGTH) {
                    OtpCell(
                        value = otpValues[i],
                        onValueChange = { newValue -> onOtpValueChanged(i, newValue) },
                        focusRequester = focusRequesters[i],
                        modifier = Modifier
                            .onKeyEvent { event ->
                                // ИСПРАВЛЕННОЕ УСЛОВИЕ
                                if (event.key == Key.Backspace && event.type == KeyEventType.KeyDown) {
                                    if (otpValues[i].isEmpty() && i > 0) {
                                        // Если текущее поле пустое и это не первое поле,
                                        // очищаем предыдущее поле и перемещаем фокус на него
                                        // onOtpValueChanged(i - 1, "") // Обновит значение
                                        // Вместо прямого вызова onOtpValueChanged, лучше просто обновить состояние
                                        // и запросить фокус, onValueChange в OtpCell сделает остальное
                                        otpValues[i - 1] = "" // Убедимся, что значение очищено
                                        focusRequesters[i - 1].requestFocus()
                                        return@onKeyEvent true // Событие обработано
                                    } else if (otpValues[i].isNotEmpty()) {
                                        // Если текущее поле не пустое, позволяем BasicTextField обработать удаление.
                                        // onValueChange в OtpCell будет вызван с пустым значением.
                                        return@onKeyEvent false
                                    }
                                    // Если текущее поле пустое и это ПЕРВОЕ поле,
                                    // ничего не делаем, событие обработано
                                    return@onKeyEvent true
                                }
                                false // Событие не обработано этим onKeyEvent
                            }
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

@Composable
fun OtpCell(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    // Используем TextFieldValue, чтобы лучше контролировать состояние, особенно курсор
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Синхронизируем textFieldValueState с внешним value
    // Это важно, если value изменяется извне (например, при удалении из предыдущего поля)
    LaunchedEffect(value) {
        if (textFieldValueState.text != value) {
            textFieldValueState = TextFieldValue(text = value, selection = TextRange(value.length))
        }
    }

    BasicTextField(
        value = textFieldValueState,
        onValueChange = { newTextFieldValue ->
            // Разрешаем только одну цифру или пустое значение (для удаления)
            if ((newTextFieldValue.text.length <= 1 && newTextFieldValue.text.all { it.isDigit() }) || newTextFieldValue.text.isEmpty()) {
                textFieldValueState = newTextFieldValue
                onValueChange(newTextFieldValue.text) // Передаем только текст во внешний обработчик
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword, // Или KeyboardType.Number
            imeAction = ImeAction.Next // Для последнего можно было бы ImeAction.Done, но автопереход важнее
        ),
        singleLine = true,
        modifier = modifier // Применяем переданный modifier (включая onKeyEvent)
            .size(50.dp)
            .focusRequester(focusRequester)
            .border(
                1.dp,
                if (textFieldValueState.text.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 8.dp), // Добавляем горизонтальные отступы для текста внутри
        textStyle = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                innerTextField()
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun OtpInputScreenPreview() {
    MaterialTheme { // Оберните в MaterialTheme для использования Material компонентов и стилей
        Surface {
            OtpInputContent({})
        }
    }
}

