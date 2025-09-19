package com.example.mangiaebasta.modal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point

@Composable
fun positionController() : Point? {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationText by remember { mutableStateOf("Fetching location...") }
    var currentLocation by remember { mutableStateOf<Point?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(fusedLocationClient) { location ->
                locationText = "Lat: ${location.latitude()}, Lon: ${location.longitude()}"
                currentLocation = location
            }
        } else {
            locationText = "Permission denied"
        }
    }

    LaunchedEffect(Unit) {
        if (checkLocationPermission(context)) {
            fetchLocation(fusedLocationClient) { location ->
                locationText = "Lat: ${location.latitude()}, Lon: ${location.longitude()}"
                currentLocation = location
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    return currentLocation
}

private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
private fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (Point) -> Unit
) {
    val task = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
    task.addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocationFetched(Point.fromLngLat(location.longitude, location.latitude))
        } else {
            onLocationFetched(Point.fromLngLat(-74.0066, 40.7135)) // Default location
        }
    }.addOnFailureListener { e ->
        onLocationFetched(Point.fromLngLat(-74.0066, 40.7135)) // Default location
    }
}