package com.rodionov.center.presentation.kind_of_sport

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.KindOfSport

@Composable
fun KindOfSportScreen() {

    val kindOfSport = remember { KindOfSport.all }
    Column {
        Text(modifier = Modifier.padding(horizontal = Dimens.SIZE_BASE.dp), text = "Выберите вид спорта")
        LazyColumn(modifier = Modifier.padding(top = Dimens.SIZE_HALF.dp)) {
            items(kindOfSport) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.SIZE_BASE.dp).clickRipple(
                    onClick = {}
                )) {
                    Text(modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp), text = item.name)
                    Icon(
                        modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp),
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "create_event"
                    )
                }
            }
        }
    }
}