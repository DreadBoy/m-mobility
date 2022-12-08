package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


class TabItem(var title: String, var screen: @Composable (vm: TimetableViewModel) -> Unit)

val tabs = listOf(
    TabItem("Priljubljene") { FavouriteStops(it) },
    TabItem("Vse postaje") { AllStops(it) },
    TabItem("Zemljevid") { TimetableMap(it) },
)

@Composable
fun Timetable(vm: TimetableViewModel) {
    var selected by rememberSaveable { mutableStateOf(2) }

    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        TabRow(selected) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected == index,
                    {
                        scope.launch { selected = index }
                    }
                ) {
                    Text(
                        tab.title,
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }

        }
        tabs[selected].screen(vm)
    }
}