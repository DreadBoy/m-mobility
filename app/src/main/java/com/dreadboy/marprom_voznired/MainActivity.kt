package com.dreadboy.marprom_voznired

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreadboy.marprom_voznired.ui.theme.MarpromVozniRedTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("vozni_red")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = TimetableViewModel(this)
        setContent {
            MarpromVozniRedTheme {
                Layout {
                    TimetableLoader(vm) {
                        Timetable(
                            it
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimetableLoader(
    vm: TimetableViewModel, content: @Composable (
        vm: TimetableViewModel
    ) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.getTimetable();
    }

    content(vm)
}

public class TimetableViewModel(context: Context) : ViewModel() {

    private val _stops = mutableStateListOf<Stop>()
    val stops: List<Stop>
        get() = _stops

    private val key = stringSetPreferencesKey("favouriteStops");
    private val dataStore = context.dataStore;
    val favouriteStops: Flow<List<String>> =
        dataStore.data.map { it[key]?.toList() ?: listOf() }

    fun getTimetable() {
        viewModelScope.launch {
            val apiService = TimetableService.getInstance()
            _stops.clear()
            _stops.addAll(apiService.getTimetable())
        }
    }

    fun saveFavourites(favourites: List<String>) {
        viewModelScope.launch {
            dataStore.edit { it[key] = favourites.toSet() }
        }
    }
}