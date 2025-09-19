package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.R
import com.example.mangiaebasta.modal.CommunicationController
import com.example.mangiaebasta.modal.Order
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.delay

@Composable
fun MapScreen(myPosition: Point?) {
    val context = LocalContext.current
    var order by remember { mutableStateOf<Order?>(null) }
    var menuName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                order = CommunicationController.getLastOrder(context)
                order?.let {
                    val menuDetails = CommunicationController.getMenuByMid(context, it.mid, it.deliveryLocation)
                    menuName = menuDetails.name
                    println("Menu Name: $menuName") // Log di debug
                }
                println("Order: $order") // Log di debug
            } catch (e: Exception) {
                println("Error fetching data: ${e.message}") // Log di debug
            }
            delay(5000)
        }
    }

Column(
        modifier = Modifier.fillMaxSize()
    ) {


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text("Menu ordinato: $menuName")
            Text("Status: ${order?.status}")
            if (order?.status == "COMPLETED") order?.deliveryTimestamp?.let { Text(it) }
            if (order?.status == "ON_DELIVERY") order?.expectedDeliveryTimestamp?.let { Text(it) }
        }
    }

        val mapViewportState = rememberMapViewportState {
            myPosition?.let {
                setCameraOptions {
                    center(it)
                    zoom(15.5)
                }
            }
        }

        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    puckBearingEnabled = true
                    puckBearing = PuckBearing.HEADING
                    enabled = true
                }
                mapViewportState.transitionToFollowPuckState()
            }
            val marker = rememberIconImage(
                key = R.drawable.position_marker,
                painter = painterResource(R.drawable.position_marker)
            )
            if (order?.currentPosition != null) {
                PointAnnotation(point = Point.fromLngLat(order!!.currentPosition.lng, order!!.currentPosition.lat)) {
                    iconImage = marker
                }
            }
        }
    }
}
