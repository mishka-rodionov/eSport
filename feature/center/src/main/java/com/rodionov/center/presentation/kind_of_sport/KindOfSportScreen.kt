package com.rodionov.center.presentation.kind_of_sport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.presentation.main.CenterViewModel
import com.rodionov.domain.models.KindOfSport
import com.rodionov.resources.R
import org.koin.androidx.compose.koinViewModel

/**
 * Экран выбора вида спорта для создания нового события.
 */
@Composable
fun KindOfSportScreen(viewModel: CenterViewModel = koinViewModel()) {
    val kindOfSport = remember { KindOfSport.all }
    val handleEffects = remember { viewModel::handleEffects }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.SIZE_BASE.dp)
        ) {
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Text(
                text = "Выберите вид спорта",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            Text(
                text = "Для создания нового соревнования выберите подходящую дисциплину",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
                contentPadding = PaddingValues(bottom = Dimens.SIZE_BASE.dp)
            ) {
                items(kindOfSport) { item ->
                    KindOfSportCard(item) {
                        if (item is KindOfSport.Orienteering) {
                            handleEffects(CenterEffects.OpenOrienteeringCreator)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Карточка выбора вида спорта.
 */
@Composable
private fun KindOfSportCard(
    kindOfSport: KindOfSport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickRipple(onClick = onClick),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.SIZE_BASE.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка вида спорта
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when (kindOfSport) {
                            is KindOfSport.Orienteering -> R.drawable.map_24dp
                            is KindOfSport.CrossCountrySki -> R.drawable.ic_build_24px // Заглушка
                            is KindOfSport.TrailRunning -> R.drawable.ic_location_on_24px // Заглушка
                        }
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getSportDisplayName(kindOfSport),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getSportDescription(kindOfSport),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Возвращает отображаемое имя для вида спорта.
 */
private fun getSportDisplayName(kindOfSport: KindOfSport): String = when (kindOfSport) {
    is KindOfSport.Orienteering -> "Спортивное ориентирование"
    is KindOfSport.CrossCountrySki -> "Лыжные гонки"
    is KindOfSport.TrailRunning -> "Трейлраннинг"
}

/**
 * Возвращает краткое описание для вида спорта.
 */
private fun getSportDescription(kindOfSport: KindOfSport): String = when (kindOfSport) {
    is KindOfSport.Orienteering -> "Бег с картой и компасом"
    is KindOfSport.CrossCountrySki -> "Гонки на лыжах по пересеченной местности"
    is KindOfSport.TrailRunning -> "Бег по природному рельефу"
}

@Preview(showBackground = true)
@Composable
private fun KindOfSportScreenPreview() {
    MaterialTheme {
        KindOfSportScreen()
    }
}
