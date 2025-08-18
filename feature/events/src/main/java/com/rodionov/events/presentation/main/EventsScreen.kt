package com.rodionov.events.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.Competition
import com.rodionov.events.R
import com.rodionov.events.data.main.EventsAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventsScreen(viewModel: EventsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = null) {
        viewModel.getEvents()
    }

    LazyColumn {
        items(state.events) {
            EventItem(it, userAction = viewModel::onAction)
        }
    }
//    Text(text = "News Screen test!")
}


@Composable
fun EventItem(
    event: Competition,
    userAction: (EventsAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier.padding(Dimens.SIZE_HALF.dp).clickRipple{
            userAction.invoke(EventsAction.EventClick(1L))
        }
    ){
        Image(
            painter = painterResource(id = R.drawable.map_24dp),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop // Масштабирование изображения
        )

        Text(
            text = event.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Отступы для заголовка
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) { // Отступы для описания
            Text(
                text = event.description,
                fontSize = 14.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2, // Ограничение строк
                overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis, // Многоточие
                onTextLayout = { layoutResult ->
                    if (!expanded) {
                        isOverflowing = layoutResult.lineCount > 1
                    }
                }
            )
            if (isOverflowing) { // Показываем "Развернуть" если строк > 2 и не развернуто
                Text(
                    text = if (expanded) "Свернуть" else "Развернуть",
                    color = Color.Blue, // Цвет кнопки "Развернуть"
                    modifier = Modifier.padding(top = 4.dp).clickRipple { expanded = !expanded } // Обработчик клика
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Пространство перед нижним рядом
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Отступы для строки
            horizontalArrangement = Arrangement.SpaceBetween // Равномерное распределение
        ) {
            Text(text = "Город: ${event.address}")
            Text(text = "Дата: ${event.date.dayOfMonth}")
            Text(text = "Время: ${event.date.monthValue}")
        }
    }
}