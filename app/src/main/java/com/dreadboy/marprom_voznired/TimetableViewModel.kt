package com.dreadboy.marprom_voznired

import android.annotation.SuppressLint
import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val _timetable = MutableStateFlow(Timetable(emptyList(), emptyList()))
    val timetable: StateFlow<Timetable> get() = _timetable

    private val key = stringSetPreferencesKey("favouriteStops")
    private val dataStore = application.dataStore
    val favouriteStops: Flow<List<String>> = dataStore.data.map { it[key]?.toList() ?: listOf() }

    init {
        getTimetable()
    }

    private fun getTimetable() {
        viewModelScope.launch {
            _timetable.value = TimetableService.getInstance().getTimetable()
        }
    }

    fun saveFavourites(favourites: List<String>) {
        viewModelScope.launch {
            dataStore.edit { it[key] = favourites.toSet() }
        }
    }
}