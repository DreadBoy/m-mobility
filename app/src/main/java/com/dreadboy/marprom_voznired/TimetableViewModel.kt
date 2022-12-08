package com.dreadboy.marprom_voznired

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val _stops = mutableStateListOf<Stop>()
    val stops: List<Stop>
        get() = _stops

    private val key = stringSetPreferencesKey("favouriteStops")
    private val dataStore = application.dataStore
    val favouriteStops: Flow<List<String>> = dataStore.data.map { it[key]?.toList() ?: listOf() }

    init {
        getTimetable()
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