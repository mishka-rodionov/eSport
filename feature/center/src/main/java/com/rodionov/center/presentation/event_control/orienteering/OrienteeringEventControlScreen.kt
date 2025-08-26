package com.rodionov.center.presentation.event_control.orienteering

import android.content.res.Configuration
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.rodionov.center.data.event_control.OrientEventControlAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrienteeringEventControlScreen(
    viewModel: OrienteeringEventControlViewModel = koinViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val isExpanded =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    OrienteeringEventControlContent(isExpanded, viewModel::onAction)
}

@Composable
fun OrienteeringEventControlContent(
    isExpanded: Boolean,
    userAction: (OrientEventControlAction) -> Unit
) {
    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue), onClick = {
                    userAction.invoke(OrientEventControlAction.OpenOrientReadCard)
                }) {
                Text(text = "Сканировать")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Green), onClick = {}) {
                Text(text = "Очистить")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Yellow), onClick = {}) {
                Text(text = "Проверить")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Red), onClick = {}) {
                Text(text = "Записать")
            }
        }
    } else {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Blue), onClick = {
                        userAction.invoke(OrientEventControlAction.OpenOrientReadCard)
                    }) {
                    Text(text = "Сканировать")
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Green), onClick = {}) {
                    Text(text = "Очистить")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Yellow), onClick = {}) {
                    Text(text = "Проверить")
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Red), onClick = {}) {
                    Text(text = "Записать")
                }
            }
        }
    }
}