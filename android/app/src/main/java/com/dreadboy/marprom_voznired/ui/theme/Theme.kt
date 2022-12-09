package com.dreadboy.marprom_voznired.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.dreadboy.marprom_voznired.R

@Composable
fun MarpromVozniRedTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors(
            primary = colorResource(R.color.primary_dark),
            onPrimary = colorResource(R.color.white),
            secondary = colorResource(R.color.secondary_dark),
            onSecondary = colorResource(R.color.black),
        ) else lightColors(
            primary = colorResource(R.color.primary_light),
            onPrimary = colorResource(R.color.black),
            secondary = colorResource(R.color.secondary_light),
            onSecondary = colorResource(R.color.black),
        ),
        content = content
    )
}
