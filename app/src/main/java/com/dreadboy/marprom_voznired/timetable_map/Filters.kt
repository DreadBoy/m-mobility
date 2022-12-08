package com.dreadboy.marprom_voznired.timetable_map

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreadboy.marprom_voznired.Route
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Filters(
    routes: List<Route>,
    content: @Composable (shownRoutes: MutableState<List<String>>) -> Unit
) {
    val scope = rememberCoroutineScope()

    val shownRoutes = rememberSaveable { mutableStateOf(routes.map { it.id }) }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (shownRoutes.value.size < routes.size)
                        TextButton({ shownRoutes.value = routes.map { it.id } }) {
                            Text("Prikaži vse linije")
                        }
                    if (shownRoutes.value.isNotEmpty())
                        TextButton({ shownRoutes.value = emptyList() }) {
                            Text("Skrij vse linije")
                        }
                }
                FlowRow(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    crossAxisSpacing = 16.dp,
                    mainAxisSpacing = 16.dp
                ) {
                    routes.forEach { route ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isChecked = shownRoutes.value.contains(route.id)
                            Checkbox(isChecked, {
                                val newRoutes =
                                    if (isChecked) shownRoutes.value.filter { it != route.id }
                                    else shownRoutes.value.plus(route.id)
                                shownRoutes.value = newRoutes
                            }, Modifier.size(20.dp))
                            Text(route.id, Modifier.padding(start = 4.dp))
                        }

                    }
                }
            }
        },
    )
    {
        content(shownRoutes)
        Column(
            Modifier
                .fillMaxSize()
                .padding(end = 4.dp), Arrangement.Bottom, Alignment.CenterHorizontally
        ) {
            Button({
                scope.launch {
                    bottomSheetState.show()
                }
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.List, null)
                    Text("Filtri")
                }
            }
        }
    }

}