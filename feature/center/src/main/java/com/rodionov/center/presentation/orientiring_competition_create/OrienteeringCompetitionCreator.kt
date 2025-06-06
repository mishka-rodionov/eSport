package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.clickRipple
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun OrienteeringCompetitionCreator(viewModel: OrienteeringCreatorViewModel = koinViewModel()) {

    val state = viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Название соревнования")
        DSTextInput(modifier = Modifier.fillMaxWidth(), text = state.value.title, onValueChanged = {
            viewModel.updateState { copy(title = it) }
        })
        Text(text = "Место проведения")
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.value.address,
            onValueChanged = {
                viewModel.updateState { copy(address = it) }
            })
        Text(text = "Дата")
        DatePickerSample()
//        DSTextInput(modifier = Modifier.fillMaxWidth(), text = state.value.date.toString(), onValueChanged = {
////            viewModel.updateState { copy(date = it.to) }
//        })
    }
}

@Composable
fun DatePickerSample() {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }


    var selectedDate by remember { mutableStateOf("") }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                selectedDate = date.format(formatter)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .clickRipple {
                datePickerDialog.show()
            }, text = selectedDate,
        enabled = false,
        readOnly = true
    )

}
