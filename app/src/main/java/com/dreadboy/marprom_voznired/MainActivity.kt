package com.dreadboy.marprom_voznired

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreadboy.marprom_voznired.ui.theme.MarpromVozniRedTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = TimetableViewModel();
        setContent {
            MarpromVozniRedTheme {
                Layout {
                    TimetableLoader(vm) { Timetable(it) }
                }
            }
        }
    }
}

@Composable
fun TimetableLoader(vm: TimetableViewModel, content: @Composable (stops: List<Stop>) -> Unit) {
    LaunchedEffect(Unit) {
        vm.getTimetable();
    }

    content(vm.stops)
}

class TimetableViewModel : ViewModel() {
    private val _stops = mutableStateListOf<Stop>()
    var errorMessage: String by mutableStateOf("")
    val stops: List<Stop>
        get() = _stops

    fun getTimetable() {
        viewModelScope.launch {
            val apiService = TimetableService.getInstance()
            try {
                _stops.clear()
                _stops.addAll(apiService.getTimetable())

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}