package com.rodionov.profile.presentation.main_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.designsystem.components.DSTextInput

@Composable
fun ProfileScreen() {
    Column {
        Text(text = "Profile screen test!")
        Text(text = "Second row234!" )
        Button(modifier = Modifier.fillMaxWidth(), onClick = {

        }) { }

        DSTextInput(modifier = Modifier.fillMaxWidth())
    }
}