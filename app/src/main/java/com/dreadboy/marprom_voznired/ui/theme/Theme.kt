package com.dreadboy.marprom_voznired.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MarpromVozniRedTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors(
            primary = Color(0xFFA62934),
            onPrimary = Color.White,
            secondary = Color(0xFFD9AA1E),
            onSecondary = Color.Black,
        ) else lightColors(
            primary = Color(0xFFF23041),
            onPrimary = Color.Black,
            secondary = Color(0xFFF2CC0F),
            onSecondary = Color.Black,
        ),
        content = content
    )
}
