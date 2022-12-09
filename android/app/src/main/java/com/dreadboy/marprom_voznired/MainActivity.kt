package com.dreadboy.marprom_voznired

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dreadboy.marprom_voznired.ui.theme.MarpromVozniRedTheme

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("vozni_red")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm by viewModels<TimetableViewModel>()
        setContent {
            MarpromVozniRedTheme {
                Layout {
                    Timetable(vm)
                }
            }
        }
    }
}