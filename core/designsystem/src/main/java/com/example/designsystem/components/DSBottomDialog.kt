package com.example.designsystem.components

import com.example.designsystem.colors.LightColors
import com.example.designsystem.theme.Dimens
import com.example.designsystem.theme.shapes


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

private val topCornerShape = RoundedCornerShape(topEnd = Dimens.SIZE_BASER.dp, topStart = Dimens.SIZE_BASER.dp)

/**
 * Нижний диалог
 * @param sheetState стейт диалога
 * @param sheetContent компоуз контект диалога
 * @param onDismiss колбек при закрытии диалога
 * @param modifier модифаер
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DSBottomDialog(
    sheetState: SheetState,
    sheetContent: @Composable () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    ModalBottomSheet(
        modifier = modifier,
        containerColor = LightColors.transparent,
        dragHandle = {
            Box(
                modifier = Modifier
                    .clip(shape = shapes.large)
//                    .background(LightColors.greyB8)
                    .size(Dimens.SIZE_TRIPLE.dp, Dimens.SIZE_QUARTER.dp)
            )
        },
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss.invoke()
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.SIZE_HALF.dp)
                        .shadow(Dimens.SIZE_QUARTER.dp, topCornerShape)
                        .background(LightColors.black, topCornerShape)
                        .navigationBarsPadding()
                ) {
                    sheetContent()
                }
            }
        },
    )
}
