package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


class TabItem(var title: String, var screen: @Composable (vm: TimetableViewModel) -> Unit)

val tabs = listOf(
    TabItem("Priljubljene") { FavouriteStops(it) },
    TabItem("Vse postaje") { AllStops(it) },
    TabItem("Zemljevid") { TimetableMap(it) },
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Timetable(vm: TimetableViewModel) {
    val pagerState = rememberPagerState(0)
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        TabRow(pagerState.currentPage) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    pagerState.currentPage == index,
                    {
                        scope.launch { pagerState.animateScrollToPage(index) }
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
        HorizontalPager(tabs.size, state = pagerState) {
            tabs[it].screen(vm)
        }
    }
}