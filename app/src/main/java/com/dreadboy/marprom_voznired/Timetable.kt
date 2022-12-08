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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Timetable(
    vm: TimetableViewModel
) {
    val stops = vm.stops;
    val favouriteStops by vm.favouriteStops.collectAsState(listOf())

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(stops) { stop ->
            val typography = MaterialTheme.typography

            ExpandableListItem({ Text(stop.name) }) {
                Column {
                    stop.routes.forEach { route ->
                        Text("Linija " + route.id, style = typography.body2)
                        Column {
                            route.lines.forEach { line ->
                                val lineId =
                                    listOf(stop.id, route.id, line.name).joinToString("\n");
                                val isFavourite = favouriteStops.contains(lineId)
                                Row(
                                    Modifier
                                        .padding(top = 8.dp, bottom = 4.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        line.name.trimStart(),
                                        Modifier.weight(1f),
                                        style = typography.body2
                                    )
                                    IconButton(
                                        {
                                            val newFavourites =
                                                if (isFavourite)
                                                    favouriteStops.filter { lineId != it }
                                                else
                                                    favouriteStops.plus(lineId)
                                            vm.saveFavourites(newFavourites)
                                        },
                                        Modifier
                                            .height(24.dp)
                                            .width(24.dp)
                                    ) {
                                        Icon(
                                            if (isFavourite)
                                                Icons.Outlined.Favorite
                                            else
                                                Icons.Outlined.FavoriteBorder,
                                            null,
                                            tint = MaterialTheme.colors.secondary
                                        )
                                    }
                                }
                                Text(line.times.joinToString(), style = typography.body2)
                            }
                        }
                        Divider(Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}