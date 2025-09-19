package com.example.mangiaebasta.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.modal.CommunicationController
import com.example.mangiaebasta.modal.StorageManager
import com.example.mangiaebasta.modal.upUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var nomeCompleto by remember { mutableStateOf("") }
    var numeroCarta by remember { mutableStateOf("") }
    var meseScadenza by remember { mutableStateOf("") }
    var annoScadenza by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sid = runBlocking { StorageManager.getSid(context).first() }
        ?: throw IllegalStateException("UID not found in DataStore")
    var lastOrder by remember { mutableStateOf("") }
    var lastOrderStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = CommunicationController.getUser(context)
            nome = user.firstName ?: ""
            cognome = user.lastName ?: ""
            nomeCompleto = user.cardFullName ?: ""
            numeroCarta = user.cardNumber ?: ""
            meseScadenza = user.cardExpireMonth.toString() ?: ""
            annoScadenza = user.cardExpireYear.toString() ?: ""
            cvv = user.cardCVV ?: ""
            lastOrder = (user.lastOid ?: "").toString()
            lastOrderStatus = user.orderStatus ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditMode) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") }
            )
            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") }
            )
            OutlinedTextField(
                value = nomeCompleto,
                onValueChange = { nomeCompleto = it },
                label = { Text("Nome Completo") }
            )
            OutlinedTextField(
                value = numeroCarta,
                onValueChange = { numeroCarta = it },
                label = { Text("Numero Carta di Credito") }
            )
            Row {
                OutlinedTextField(
                    value = meseScadenza,
                    onValueChange = { meseScadenza = it },
                    label = { Text("Mese Scadenza") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = annoScadenza,
                    onValueChange = { annoScadenza = it },
                    label = { Text("Anno Scadenza") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text("CVV") }
            )
        } else {
            Text("Nome: $nome")
            Text("Cognome: $cognome")
            Text("Nome Completo: $nomeCompleto")
            Text("Numero Carta di Credito: $numeroCarta")
            Text("Mese Scadenza: $meseScadenza")
            Text("Anno Scadenza: $annoScadenza")
            Text("CVV: $cvv")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary),
            onClick = {
            if (isEditMode) {
                coroutineScope.launch {
                    val user = upUser(
                        firstName = nome,
                        lastName = cognome,
                        cardFullName = nomeCompleto,
                        cardNumber = numeroCarta,
                        cardExpireMonth = meseScadenza.toIntOrNull(),
                        cardExpireYear = annoScadenza.toIntOrNull(),
                        cardCVV = cvv,
                        sid = sid,
                    )
                    CommunicationController.updateUser(context, user)
                }
            }
            isEditMode = !isEditMode
        }) {
            Text(if (isEditMode) "Salva" else "Modifica")
        }
        Text("Id ultimo ordine:$lastOrder")
        Text("Stato ultimo ordine:$lastOrderStatus")
    }
}