package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun Timetable(vm: TimetableViewModel) {
    val favouriteStops by vm.favouriteStops.collectAsState(listOf())
    val stops = vm.stops

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(favouriteStops, key = { it }) { FavouriteStop(it, vm) }
        items(stops.sortedBy { it.name }, key = { it.id }) { Stop(it, vm) }
    }
}