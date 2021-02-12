package com.omelan.cofi.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

private val DarkColorPalette = darkColors(
    primary = brown300,
    primaryVariant = brown700,
    secondary = iconBackground
)

private val LightColorPalette = lightColors(
    primary = brown500,
    primaryVariant = brown700,
    secondary = iconBackground

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val spacingDefault = 16.dp

@Composable
fun CofiTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
    ) {
        ProvideWindowInsets(content = content)
    }
}