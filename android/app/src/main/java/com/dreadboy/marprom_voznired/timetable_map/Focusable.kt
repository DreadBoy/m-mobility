package com.dreadboy.marprom_voznired.timetable_map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreadboy.marprom_voznired.Route
import com.dreadboy.marprom_voznired.TimetableViewModel

@Composable
fun Focusable(
    vm: TimetableViewModel,
    routes: List<Route>,
    shownRoutes: MutableState<List<String>>,
    content: @Composable (focusRoute: MutableState<String?>, focusStop: MutableState<String?>) -> Unit
) {

    val stops = vm.timetable.collectAsState().value.timetable
    val favouriteStops by vm.favouriteStops.collectAsState(listOf())

    val focusRoute = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val focusStop = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    content(focusRoute, focusStop)

    if (focusRoute.value != null) {
        val route = routes.first { it.id == focusRoute.value }
        AlertDialog(
            { focusRoute.value = null },
            {},
            title = { Text("Linija " + route.id) },
            text = {
                if (shownRoutes.value.size == 1 && shownRoutes.value.contains(route.id))
                    OutlinedButton({
                        shownRoutes.value = routes.map { it.id }
                        focusRoute.value = null
                    }) {
                        Text("Prikaži vse linije")
                    }
                else
                    OutlinedButton({
                        shownRoutes.value = listOf(route.id)
                        focusRoute.value = null
                    }) {
                        Text("Prikaži samo to linijo")
                    }
            })
    }
    if (focusStop.value != null) {
        val stop = stops.first { it.id == focusStop.value }

        AlertDialog(
            { focusStop.value = null },
            {},
            title = { Text(stop.name) },
            text = {
                val typography = MaterialTheme.typography

                Column(
                    Modifier
                        .padding(top = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    stop.routes.filter { shownRoutes.value.contains(it.id) }.forEach { route ->
                        Text("Linija " + route.id, style = typography.body2)
                        Column {
                            route.lines.forEach { line ->
                                val lineId =
                                    listOf(stop.id, route.id, line.name).joinToString("\n")
                                val isFavourite = favouriteStops.contains(lineId)
                                Row(
                                    Modifier
                                        .padding(top = 8.dp, bottom = 4.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        line.name,
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
            })
    }
}