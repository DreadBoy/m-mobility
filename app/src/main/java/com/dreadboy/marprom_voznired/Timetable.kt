package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Timetable(
    vm: TimetableViewModel
) {
    val stops = vm.stops;
    val favouriteStops by vm.favouriteStops.collectAsState(listOf());

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(stops) { stop ->
            val isFavourite = favouriteStops.contains(stop.id)
            ListItem(
                text = { Text(stop.name) },
                secondaryText = { Text("Linije: " + stop.routes.joinToString(", ") { it.id }) },
                trailing = {
                    IconButton(
                        onClick = {
                            val newFavourites =
                                if (isFavourite) favouriteStops.filter { stop.id != it } else favouriteStops.plus(
                                    stop.id
                                )
                            vm.saveFavourites(newFavourites)
                        },
                    ) {
                        Icon(
                            if (isFavourite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            null,
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }
            )
        }
    }
}