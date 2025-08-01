package com.example.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ExposedDropdownMenuOutlined(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemToString: (T) -> String = { it.toString() } // Функция для отображения элемента как строки
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        DSTextInput(
            text = selectedItem?.let { itemToString(it) } ?: "",
            onValueChanged = {}, // Не изменяется напрямую текстом
            readOnly = true,    // Делаем поле только для чтения
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
//            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor() // Важно для правильного позиционирования меню
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToString(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// Пример использования и превью
@Preview(showBackground = true)
@Composable
fun ExposedDropdownMenuOutlinedPreview() {
    val options = listOf("Опция 1", "Опция 2", "Длинная Опция три", "Опция 4")
    var selectedOption by remember { mutableStateOf<String?>(null) } // Может быть null, если ничего не выбрано

    val complexOptions = listOf(
        PreviewData("ID1", "Яблоко"),
        PreviewData("ID2", "Банан"),
        PreviewData("ID3", "Вишня")
    )
    var selectedComplexOption by remember { mutableStateOf<PreviewData?>(complexOptions[0]) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column {
                ExposedDropdownMenuOutlined(
                    label = "Простой выбор",
                    items = options,
                    selectedItem = selectedOption,
                    onItemSelected = { selectedOption = it },
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuOutlined(
                    label = "Выбор объекта",
                    items = complexOptions,
                    selectedItem = selectedComplexOption,
                    onItemSelected = { selectedComplexOption = it },
                    itemToString = { it.displayName }, // Указываем, как отображать объект
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

data class PreviewData(val id: String, val displayName: String)

