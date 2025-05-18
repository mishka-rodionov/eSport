package com.rodionov.center.presentation.kind_of_sport

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.rodionov.domain.models.KindOfSport

@Composable
fun KindOfSportScreen() {

    val kindOfSport = remember { KindOfSport.all }
    val intSource = remember { MutableInteractionSource() }
    Column {
        Text(modifier = Modifier.padding(horizontal = 16.dp), text = "Выберите вид спорта")
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(kindOfSport) { item ->
                Row(modifier = Modifier.padding(vertical = 16.dp).padding(horizontal = 16.dp).clickable(
                    interactionSource = intSource, indication = null, onClick = {}
                )) {
                    Text(item.name)
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "create_event"
                    )
                }
            }
        }
    }
}