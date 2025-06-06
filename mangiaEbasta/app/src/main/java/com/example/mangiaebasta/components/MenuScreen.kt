package com.example.mangiaebasta.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.modal.database.MenuDatabase
import com.example.mangiaebasta.modal.database.MenuEntity
import com.example.mangiaebasta.modal.CommunicationController
import com.example.mangiaebasta.modal.Menu
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun MenuScreen(currentPosition: Point, onDetailsClick: (Menu) -> Unit) {
    val context = LocalContext.current
    val menuList = remember { mutableStateOf<List<Menu>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val menuDao = MenuDatabase.getDatabase(context).menuDao()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
                val menusFromServer = CommunicationController.getMenu(context, currentPosition)
                menusFromServer.map { menu ->
                    val dbMenu = menuDao.getMenuByIdfromDb(menu.mid)
                    if (dbMenu == null || dbMenu.imageVersion != menu.imageVersion) {
                        val base64Image = CommunicationController.getMenuImg(context, menu.mid).base64
                        val menuEntity = MenuEntity(
                            mid = menu.mid,
                            imageBase64 = base64Image,
                            imageVersion = menu.imageVersion
                        )
                        menuDao.insertMenu(menuEntity)
                    }
                }
                menuList.value = menusFromServer
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuList.value) { menu ->
                MenuItem(menu, onDetailsClick)
            }
        }
    }
}

@Composable
fun MenuItem(menu: Menu, onDetailsClick: (Menu) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var base64Image by remember { mutableStateOf<String?>(null) }
    val menuDao = MenuDatabase.getDatabase(context).menuDao()

    LaunchedEffect(menu.mid) {
        coroutineScope.launch {
            val dbMenu = menuDao.getMenuByIdfromDb(menu.mid)
            base64Image = dbMenu?.imageBase64
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            base64Image?.let { base64 ->
                Base64Viewer(menu, base64)
            }
            Text(text = menu.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Costo: ${menu.price}€", style = MaterialTheme.typography.bodyLarge)
            Text(text = menu.shortDescription, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Tempo di consegna: ${menu.deliveryTime} minuti", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { onDetailsClick(menu) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Dettagli", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Base64Viewer(menu: Menu, base64: String, modifier: Modifier = Modifier) {
    val byteArray = Base64.decode(base64)
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = menu.shortDescription,
        modifier = modifier.size(128.dp)
    )
}