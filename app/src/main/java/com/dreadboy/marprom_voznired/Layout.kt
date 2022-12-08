package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Layout(content: @Composable BoxScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar({ Text("Marprom vozni red") }, backgroundColor = MaterialTheme.colors.primary)
        }) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}