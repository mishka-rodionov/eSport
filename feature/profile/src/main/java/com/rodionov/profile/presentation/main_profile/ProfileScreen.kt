package com.rodionov.profile.presentation.main_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.colors.LightColors
import com.example.designsystem.theme.Dimens
import com.rodionov.profile.data.ProfileAction
import com.rodionov.profile.data.ProfileState
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Экран профиля пользователя.
 */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()
    }

    if (state.user == null) {
        UnauthorizedUser(viewModel::onAction)
    } else {
        AuthorizedUser(state, viewModel::onAction)
    }

}

/**
 * Отображение для неавторизованного пользователя.
 */
@Composable
fun UnauthorizedUser(userAction: (ProfileAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(Dimens.SIZE_TWO.dp), verticalArrangement = Arrangement.Center) {

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            userAction.invoke(ProfileAction.ToRegister)
        }) {
            Text(text = "Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_TWO.dp).fillMaxWidth())

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
fun AuthorizedUser(state: ProfileState, onAction: (ProfileAction) -> Unit) {
    val user = state.user
    Column(modifier = Modifier.fillMaxSize().padding(Dimens.SIZE_TWO.dp)) {
        // Верхняя часть: Аватар, ФИО и кнопка редактирования
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.play_arrow_24px), // Используем стандартный ресурс профиля
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(Dimens.SIZE_TWO.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user?.firstName} ${user?.lastName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = DateTimeFormat.transformLongToDisplayDate(user?.birthDate),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onAction(ProfileAction.ToProfileEditor) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_QUARTER.dp))

        // Пункты меню
        ProfileMenuItem(text = "Предстоящие старты", onClick = { /* TODO */ })
        ProfileMenuItem(text = "Мои результаты", onClick = { /* TODO */ })
        ProfileMenuItem(text = "О приложении", onClick = { /* TODO */ })
    }
}

/**
 * Элемент меню в профиле пользователя.
 */
@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.SIZE_TWO.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = Dimens.SIZE_SINGLE.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = LightColors.greyB8)
    }
}
