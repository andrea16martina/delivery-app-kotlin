package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(currentScreen: String, onScreenChange: (String) -> Unit) {
    BottomAppBar {
        // Utilizziamo un Row per distribuire i pulsanti equamente
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Pulsante Menu
            IconButton(
                onClick = { onScreenChange("menu") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Menu")
            }

            // Pulsante Mappa
            IconButton(
                onClick = { onScreenChange("map") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mappa")
            }

            // Pulsante Profilo
            IconButton(
                onClick = { onScreenChange("profile") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Profilo")
            }
        }
    }
}
