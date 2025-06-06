package com.example.mangiaebasta.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.modal.CommunicationController
import com.example.mangiaebasta.modal.Menu
import com.example.mangiaebasta.modal.MenuDetails
import com.example.mangiaebasta.modal.database.MenuDatabase
import com.example.mangiaebasta.modal.positionController
import kotlinx.coroutines.launch
import com.example.mangiaebasta.modal.database.OrderEntity

@Composable
fun DetailScreen(context: Context, menu: Menu) {
    var menuDetails by remember { mutableStateOf<MenuDetails?>(null) }
    var menuImg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val dao = MenuDatabase.getDatabase(context).menuDao()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val currentPosition = positionController()
    val orderDao = MenuDatabase.getDatabase(context).orderDao()

    LaunchedEffect(menu) {
        coroutineScope.launch {
            menuDetails = CommunicationController.getMenuDetails(context, menu)
            menuImg = dao.getMenuByIdfromDb(menu.mid)?.imageBase64
        }
    }

    menuDetails?.let { details ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            menuImg?.let { base64 ->
                val byteArray = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = details.shortDescription,
                    modifier = Modifier.size(128.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = details.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Price: ${details.price}€", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Description: ${details.shortDescription}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Delivery Time: ${details.deliveryTime} minutes", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Long Description: ${details.longDescription}", style = MaterialTheme.typography.bodyMedium)
            Button( modifier = Modifier
                .background(MaterialTheme.colorScheme.primary),
                onClick = {
                coroutineScope.launch {
                    try {
                        let { currentPosition }?.let {
                            val order = CommunicationController.buyMenu(context, menu.mid,
                                it
                            )
                            val orderEntity = OrderEntity(
                                oid = order.oid,
                                mid = order.mid,
                                uid = order.uid,
                                currentPosition = order.currentPosition,
                                status = order.status,
                                creationTimestamp = order.creationTimestamp,
                                deliveryLocation = order.deliveryLocation,
                                deliveryTimestamp = order.deliveryTimestamp,
                                expectedDeliveryTimestamp = order.expectedDeliveryTimestamp
                            )
                            orderDao.insertOrder(orderEntity)

                        }
                    } catch (e: Exception) {
                        when (e.message) {
                            "403" -> {
                                showErrorDialog = true
                                errorMsg = "Numero della carta non valido. Imposta un numero di carta valido e riprova."
                            }
                            "profile not complete" -> {
                                showErrorDialog = true
                                errorMsg = "Completa il profilo prima di effettuare un ordine."
                            }
                            "ON_DELIVERY" -> {
                                showErrorDialog = true
                                errorMsg = "Hai un altro ordine in corso. Riprova più tardi."
                            }
                            else -> {
                                showErrorDialog = true
                                errorMsg = "Ordine effettuato con successo."
                            }
                        }
                    }
                }
            }) {Text("Acquista") }
        }
    }
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Errore") },
            text = { Text(errorMsg) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}