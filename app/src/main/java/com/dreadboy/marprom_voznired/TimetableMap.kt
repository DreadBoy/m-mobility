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
import com.dreadboy.marprom_voznired.timetable_map.Filters
import com.dreadboy.marprom_voznired.timetable_map.Focusable
import com.dreadboy.marprom_voznired.timetable_map.StopIcon
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimetableMap(vm: TimetableViewModel) {
    val stops = vm.timetable.collectAsState().value.timetable
    val polylines = vm.timetable.collectAsState().value.polylines

    if (stops.isEmpty() || polylines.isEmpty()) return

    val routesWithStops = stops.flatMap { stop ->
        stop.routes.map { route ->
            object {
                val stop = stop
                val route = route
            }
        }
    }.groupBy { it.route.id }.map { entry ->
        object {
            val route = entry.value.first().route
            val stops = entry.value.map { it.stop }
        }
    }

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


    Filters(routesWithStops.map { it.route }) { shownRoutes ->
        Focusable(vm, routesWithStops.map { it.route }, shownRoutes) { focusRoute, focusStop ->
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                googleMapOptionsFactory = {
                    GoogleMapOptions().compassEnabled(false).rotateGesturesEnabled(false)
                        .tiltGesturesEnabled(false)
                },
                properties = MapProperties(
                    isMyLocationEnabled = fineLocationState.hasPermission,
                ),
            ) {

                polylines.filter { shownRoutes.value.contains(it.routeId) }.forEach { route ->
                    route.polyline.forEach {
                        Polyline(
                            it.map { coordinates -> LatLng(coordinates.lat, coordinates.lng) },
                            true,
                            androidx.compose.ui.graphics.Color(
                                Color.parseColor(route.color)
                            ),
                        ) {
                            focusRoute.value = route.routeId
                        }
                    }
                }
                routesWithStops.filter { shownRoutes.value.contains(it.route.id) }
                    .forEach { route ->
                        val color =
                            Color.parseColor(polylines.first { it.routeId == route.route.id }.color)
                        route.stops.forEach { stop ->
                            Marker(MarkerState(LatLng(stop.coordinates.lat, stop.coordinates.lng)),
                                icon = StopIcon(color).toBitmapDescriptor(),
                                onClick = {
                                    focusStop.value = stop.id
                                    true
                                })
                        }
                    }
            }
            if (!fineLocationState.hasPermission) Column(
                Modifier
                    .fillMaxSize()
                    .padding(end = 4.dp), Arrangement.Top, Alignment.End
            ) {
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
    }

}