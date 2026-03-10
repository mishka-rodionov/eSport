package com.rodionov.profile.presentation.main_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rodionov.profile.data.ProfileAction
import com.rodionov.profile.data.ProfileState
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Экран профиля пользователя.
 */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        viewModel.getCurrentUser()
    }

    if (state.user == null) {
        UnauthorizedUser(viewModel::onAction)
    } else {
        AuthorizedUser(state)
    }

}

/**
 * Отображение для неавторизованного пользователя.
 */
@Composable
fun UnauthorizedUser(userAction: (ProfileAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            userAction.invoke(ProfileAction.ToRegister)
        }) {
            Text(text = "Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            userAction.invoke(ProfileAction.ToAuth)
        }) {
            Text(text = "Войти")
        }

    }
}

/**
 * Отображение данных авторизованного пользователя.
 */
@Composable
fun AuthorizedUser(state: ProfileState) {
    val user = state.user
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Имя: ${user?.firstName}")
        Text(text = "Фамилия: ${user?.lastName}")
        // Используем DateTimeFormat для отображения даты рождения из Long
        Text(text = "Дата рождения: ${DateTimeFormat.transformLongToDisplayDate(user?.birthDate)}")
    }
}
