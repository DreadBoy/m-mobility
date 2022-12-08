package com.dreadboy.marprom_voznired

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreadboy.marprom_voznired.ui.theme.MarpromVozniRedTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("vozni_red")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm by viewModels<TimetableViewModel>()
        setContent {
            MarpromVozniRedTheme {
                Layout {
                    Column {
                        Timetable(vm)
                    }
                }
            }
        }
    }
}

public class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val _stops = mutableStateListOf<Stop>()
    val stops: List<Stop>
        get() = _stops

    private val key = stringSetPreferencesKey("favouriteStops");
    private val dataStore = application.dataStore;
    val favouriteStops: Flow<List<String>> = dataStore.data.map { it[key]?.toList() ?: listOf() }

    init {
        getTimetable();
    }

    private fun getTimetable() {
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