package com.omelan.cofi.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets

private val DarkColorPalette = darkColors(
    primary = brown300,
    primaryVariant = brown700,
    secondary = iconBackground,
)

private val LightColorPalette = lightColors(
    primary = brown500,
    primaryVariant = brown700,
    secondary = iconBackground,
    secondaryVariant = iconBackground,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun createMaterialYouPallets() = Pair(
    lightColors(
        primary = colorResource(id = android.R.color.system_accent1_400),
        primaryVariant = colorResource(id = android.R.color.system_accent1_400),
        secondary = colorResource(id = android.R.color.system_accent1_600),
        secondaryVariant = colorResource(id = android.R.color.system_accent1_600),


        background = colorResource(id = android.R.color.system_neutral2_100),
        surface = colorResource(id = android.R.color.system_neutral2_50),
        onPrimary = colorResource(id = android.R.color.system_neutral1_900),
        onSecondary = colorResource(id = android.R.color.system_neutral2_700),
//            onBackground = Color.Black,
        onSurface = colorResource(id = android.R.color.system_neutral1_900),
    ), darkColors(
        primary = colorResource(id = android.R.color.system_accent1_600),
        primaryVariant = colorResource(id = android.R.color.system_accent1_500),
        secondary = colorResource(id = android.R.color.system_accent1_600),
        secondaryVariant = colorResource(id = android.R.color.system_accent1_500),


        background = colorResource(id = android.R.color.system_neutral2_1000),
        surface = colorResource(id = android.R.color.system_neutral2_800),
        onPrimary = colorResource(id = android.R.color.system_neutral1_100),
        onSecondary = colorResource(id = android.R.color.system_neutral1_100),
//            onBackground = Color.Black,
        onSurface = colorResource(id = android.R.color.system_neutral1_100),
    )
)

val spacingDefault = 16.dp

@SuppressLint("NewApi")
@ExperimentalAnimatedInsets
@Composable
fun CofiTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val (lightColors, darkColors) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        createMaterialYouPallets()
    } else {
        Pair(LightColorPalette, DarkColorPalette)
    }
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
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false, content = content)
    }
}