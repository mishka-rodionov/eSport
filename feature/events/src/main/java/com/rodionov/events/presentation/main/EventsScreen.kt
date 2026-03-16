package com.rodionov.events.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.resources.R
import com.rodionov.events.data.main.EventsAction
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Экран списка событий.
 */
@Composable
fun EventsScreen(viewModel: EventsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = null) {
        viewModel.getEvents()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp), // Отступ между карточками 8 dp
        contentPadding = PaddingValues(Dimens.SIZE_TWO.dp)
    ) {
        itemsIndexed(state.events) { index, item ->
            EventItem(item, userAction = viewModel::onAction, index)
        }
    }
}

/**
 * Карточка события в списке.
 *
 * @param event Модель данных соревнования.
 * @param userAction Коллбэк для обработки действий пользователя.
 * @param index Индекс элемента в списке.
 */
@Composable
fun EventItem(
    event: Competition,
    userAction: (EventsAction) -> Unit,
    index: Int
) {
    var expanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickRipple {
                userAction.invoke(EventsAction.EventClick(index))
            },
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp), // Скругление карточки 16 dp
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Изображение события с бейджем вида спорта
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.forest),
                    contentDescription = "Event Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                
                // Бейдж вида спорта
                Box(
                    modifier = Modifier
                        .padding(Dimens.SIZE_TWO.dp)
                        .clip(RoundedCornerShape(Dimens.SIZE_BASE.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = event.kindOfSport.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(modifier = Modifier.padding(Dimens.SIZE_TWO.dp)) {
                // Заголовок
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))

                // Дата и место
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(com.rodionov.resources.R.drawable.ic_date_range_24px),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = DateTimeFormat.transformLongToDisplayDate(event.date),
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.width(Dimens.SIZE_TWO.dp))
                    
                    Icon(
                        imageVector = ImageVector.vectorResource(com.rodionov.resources.R.drawable.ic_location_on_24px),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.address,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))

                // Описание с логикой развертывания
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    onTextLayout = { layoutResult ->
                        if (!expanded) {
                            isOverflowing = layoutResult.lineCount > 2
                        }
                    }
                )
                
                if (isOverflowing) {
                    Text(
                        text = if (expanded) "Свернуть" else "Подробнее...",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { expanded = !expanded }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventItemPreview() {
    val mockEvent = Competition(
        title = "Чемпионат по спортивному ориентированию \"Осенний лес 2024\"",
        date = System.currentTimeMillis(),
        kindOfSport = KindOfSport.Orienteering,
        description = "Традиционные соревнования в живописном лесу. Вас ждут интересные дистанции различной сложности, электронная отметка и горячий чай на финише. Приглашаются все желающие!",
        address = "Загородный парк, г. Владимир",
        mainOrganizer = "admin",
        coordinates = Coordinates(0.0, 0.0)
    )
    
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EventItem(
                event = mockEvent,
                userAction = {},
                index = 0
            )
        }
    }
}
