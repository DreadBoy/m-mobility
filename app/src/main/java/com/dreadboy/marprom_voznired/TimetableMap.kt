package com.dreadboy.marprom_voznired

import android.Manifest
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimetableMap(vm: TimetableViewModel) {
    val stops = vm.timetable.collectAsState().value.timetable
    val routes = vm.timetable.collectAsState().value.polylines

    if (stops.isEmpty() || routes.isEmpty()) return

    val lats = stops.map { it.coordinates.lat }
    val lngs = stops.map { it.coordinates.lng }
    val center = LatLng(
        lats.average(),
        lngs.average(),
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 13f)
    }

    val fineLocationState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        googleMapOptionsFactory = {
            GoogleMapOptions()
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
        },
        properties = MapProperties(
            isMyLocationEnabled = fineLocationState.hasPermission,
        ),
    ) {
        routes.forEach { route ->
            route.polyline.forEach {
                Polyline(
                    it.map { coordinates -> LatLng(coordinates.lat, coordinates.lng) },
                    color = androidx.compose.ui.graphics.Color(
                        Color.parseColor(route.color)
                    )
                )
            }

        }
    }
    if (!fineLocationState.hasPermission)
        Column(
            Modifier
                .fillMaxSize()
                .padding(end = 4.dp), Arrangement.Top, Alignment.End) {
            Button({
                fineLocationState.launchPermissionRequest()
            }) {
                Row {
                    Icon(Icons.Outlined.LocationOn, null)
                    Text("Moja lokacija")
                }
            }
        }
}