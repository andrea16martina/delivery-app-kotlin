package com.example.mangiaebasta

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.mangiaebasta.components.*
import com.example.mangiaebasta.modal.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import com.example.mangiaebasta.ui.theme.MangiaEbastaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MangiaEbastaTheme {
                val context = LocalContext.current
                var isFirstRun by remember { mutableStateOf(false) }
                var currentScreen by remember { mutableStateOf("menu") }
                var selectedMenu by remember { mutableStateOf<Menu?>(null) }

                LaunchedEffect(Unit) {
                    val sid = runBlocking { StorageManager.getSid(context).first() }
                    isFirstRun = sid == null
                    if (isFirstRun) {
                        runBlocking {
                            getSidAndUid(context)
                        }
                    }
                    lifecycleScope.launch {
                        StorageManager.getLastPage(context).collect { page ->
                            page?.let {
                                currentScreen = it
                                if (it == "detail") {
                                    selectedMenu = StorageManager.getSelectedMenu(context).first()
                                }
                            }
                        }
                    }
                }
                NavigationApp(currentScreen, selectedMenu) { newScreen, menu ->
                    currentScreen = newScreen
                    selectedMenu = menu
                    lifecycleScope.launch {
                        StorageManager.setLastPage(context, newScreen)
                        menu?.let { StorageManager.setSelectedMenu(context, it) }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationApp(currentScreen: String, selectedMenu: Menu?, onScreenChange: (String, Menu?) -> Unit) {
    val context = LocalContext.current
    val currentPosition = positionController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentScreen) { newScreen ->
                onScreenChange(newScreen, null)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                "menu" -> currentPosition?.let {
                    MenuScreen(it) { menu ->
                        onScreenChange("detail", menu)
                    }
                }
                "map" -> MapScreen(currentPosition)
                "profile" -> ProfileScreen()
                "detail" -> selectedMenu?.let { DetailScreen(context, it) }
            }
        }
    }
}

private suspend fun getSidAndUid(context: Context) {
    val response = CommunicationController.createUser()
    StorageManager.setSid(context, response.sid)
    StorageManager.setUid(context, response.uid.toString())
}