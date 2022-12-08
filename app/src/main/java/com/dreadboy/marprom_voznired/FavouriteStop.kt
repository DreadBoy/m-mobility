package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavouriteStops(vm: TimetableViewModel) {
    val favouriteStops by vm.favouriteStops.collectAsState(listOf())

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(favouriteStops, key = { it }) { FavouriteStop(it, vm) }
    }
}

@Composable
fun FavouriteStop(lineId: String, vm: TimetableViewModel) {
    val stops = vm.timetable.collectAsState().value.timetable

    val (stopId, routeId, lineName) = lineId.split("\n")
    val stop = stops.firstOrNull { it.id == stopId }
    val route = stop?.routes?.firstOrNull { it.id == routeId }
    val line = route?.lines?.firstOrNull { it.name == lineName }

    if (line != null) Card(
        Modifier
            .padding(16.dp, 16.dp, 16.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("Linija " + route.id + " - " + stop.name)
            Text(line.name, style = MaterialTheme.typography.caption)
            NextBus(line.times)
        }
    }
}