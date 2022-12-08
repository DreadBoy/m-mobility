package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Timetable(stops: List<Stop>) {
    val routes = stops
        .flatMap { stop ->
            stop.routes.map { route ->
                object {
                    var stop = stop
                    val route = route
                }
            }
        }
        .groupBy { it.route.id }
        .map {
            object {
                val route = it.value[0].route
                val stops = it.value.map { obj -> obj.stop }
            }
        }

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(stops) { stop ->
            ListItem(
                text = { Text(stop.name) },
                secondaryText = { Text("Linije: " + stop.routes.joinToString(", ") { it.id })},
            )
        }
    }
}