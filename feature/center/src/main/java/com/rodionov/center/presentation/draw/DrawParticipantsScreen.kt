package com.rodionov.center.presentation.draw

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.draw.DrawAction
import com.rodionov.center.presentation.participant_list.ParticipantList
import com.rodionov.resources.R
import org.koin.androidx.compose.koinViewModel

/**
 * Экран для жеребьевки участников соревнования.
 * Позволяет просмотреть текущий список и запустить процесс автоматического распределения стартовых номеров.
 */
@Composable
fun DrawParticipantsScreen(viewModel: DrawViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val userAction = remember { viewModel::onAction }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок и описание
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp)
            ) {
                Text(
                    text = "Жеребьёвка",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                
                Text(
                    text = "Нажмите на кнопку старта, чтобы автоматически распределить стартовые номера между участниками.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (state.participants.isEmpty()) {
                    EmptyDrawView()
                } else {
                    ParticipantList(
                        participants = state.participants,
                        onEditClick = {}, // На этом экране только просмотр/жеребьевка
                        onDeleteClick = {}
                    )
                }

                // Кнопки запуска жеребьевки
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Dimens.SIZE_BASE.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { userAction.invoke(DrawAction.StartGroupDrawOperation) },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(text = "Жеребьёвка по группам", fontWeight = FontWeight.Bold)
                        }
                    )
                    ExtendedFloatingActionButton(
                        onClick = { userAction.invoke(DrawAction.StartDrawOperation) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(text = "Провести жеребьёвку", fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Вид при отсутствии участников для жеребьевки.
 */
@Composable
private fun EmptyDrawView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = "Нет участников для проведения жеребьёвки.\nСначала добавьте их в список участников.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
