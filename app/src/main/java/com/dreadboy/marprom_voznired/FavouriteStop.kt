package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavouriteStop(lineId: String, vm: TimetableViewModel) {
    val stops = vm.stops

    val (stopId, routeId, lineName) = lineId.split("\n")
    val stop = stops.firstOrNull { it.id == stopId }
    val route = stop?.routes?.firstOrNull { it.id == routeId }
    val line = route?.lines?.firstOrNull { it.name == lineName }

    if (line != null)
        Card(
            Modifier
                .padding(16.dp, 16.dp, 16.dp)
                .fillMaxWidth()
        ) {
            Column(Modifier.padding(8.dp)) {
                Text("Linija " + route.id + " - " + stop.name)
                Text(line.name.trimStart(), style = MaterialTheme.typography.caption)
                NextBus(line.times)
            }
        }
}